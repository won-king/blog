package com.git.blog.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangke on 2019-07-27 10:45
 */
public class SpecifiedBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {
    private static final Logger log= LoggerFactory.getLogger(SpecifiedBeanPostProcessor.class);

    private final Map<String,InjectionMetadata> injectionMetadataCache=new ConcurrentHashMap<>(256);
    private ConfigurableBeanFactory beanFactory;

    private int order=Integer.MAX_VALUE;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if(beanFactory instanceof ConfigurableBeanFactory){
            this.beanFactory= (ConfigurableBeanFactory) beanFactory;
        }else {
            throw new IllegalArgumentException("SpecifiedBeanPostProcess requires a ConfigurableBeanFactory: "+beanFactory);
        }
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition rootBeanDefinition, Class<?> aClass, String s) {
        InjectionMetadata metadata;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return super.postProcessPropertyValues(pvs, pds, bean, beanName);
    }

    private InjectionMetadata findAutowiredMetadata(String beanName, Class<?> cladd, PropertyValues pvs){
        String cacheKey= StringUtils.hasLength(beanName) ? beanName:cladd.getName();
        InjectionMetadata metadata=injectionMetadataCache.get(cacheKey);
        if(InjectionMetadata.needsRefresh(metadata, cladd)){
            synchronized (injectionMetadataCache){
                metadata=injectionMetadataCache.get(cacheKey);
                if(InjectionMetadata.needsRefresh(metadata, cladd)){
                    if(metadata!=null){
                        metadata.clear(pvs);
                    }
                    metadata=buildAutowiredMetadata(cladd);
                    injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildAutowiredMetadata(Class<?> cladd){
        //todo 这里主要做的工作，就是找出所有包含注解的元数据（包括，字段上的注解，方法上的注解）
        // 当然还有一个细节，就是你支不支持递归注入，就是父类中如果也包含注解，是否需要父类的也注入
        // spring原生注解，AutowiredAnnotationBeanPostProcessor，这里里面是支持递归注入的
        // 鱼泡泡实现的那个，其实是完全仿照这个来实现的，区别在于，后者没有支持递归注入
        // 这里我要支持一下递归的注入
        // 注：静态字段不支持注入，原因是静态字段是在字节码加载期间初始化的，而spring生命周期，都是集中在对象实例化前后这个时期
        List<InjectionMetadata.InjectedElement> elements=new LinkedList<>();
        Class targetClass=cladd;
        do {
            List<InjectionMetadata.InjectedElement> currElements=new LinkedList<>();
            currElements.addAll(findAutowiredFieldMetadata(targetClass));
            currElements.addAll(findAutowiredMethodMetadata(targetClass));
            //这里的目的是为了把父类的注入插入到子类之前，addAll指定位置就会从那个位置开始插入新的，把原来的往后挤
            elements.addAll(0, currElements);
            targetClass=targetClass.getSuperclass();
        }while (targetClass!=null && targetClass!=Object.class);
        return new InjectionMetadata(cladd, elements);
    }
    //如果想支持字段和方法的两种注入方式，就需要都找出来包含注解的内容
    private List<InjectionMetadata.InjectedElement> findAutowiredFieldMetadata(Class<?> cladd){

        return Collections.emptyList();
    }
    private List<InjectionMetadata.InjectedElement> findAutowiredMethodMetadata(Class<?> cladd){
        return Collections.emptyList();
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
