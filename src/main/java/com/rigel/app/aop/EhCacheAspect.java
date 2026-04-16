package com.rigel.app.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EhCacheAspect {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Map<String, Object> cache;

	public EhCacheAspect() {
		cache = new HashMap<String, Object>();
	}

	@Pointcut("execution(@org.springframework.cache.annotation.Cacheable * *.*(..))")
	@SuppressWarnings("unused")
	private void cacheAble() {
	}
	
	@Pointcut("execution(@org.springframework.cache.annotation.CacheEvict * *.*(..))")
	@SuppressWarnings("unused")
	private void cacheEvict() {
	}

	@Around("cacheAble()")
	public Object aroundCachedMethods(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		log.debug("Execution of Cacheable method catched");
		return catcheCall(thisJoinPoint);
	}
	
//	@Around("cacheEvict()")
//	public Object aroundCacheEvictMethods(ProceedingJoinPoint thisJoinPoint) throws Throwable {
//		log.debug("Execution of cacheEvict method catched");
//		return catcheCall(thisJoinPoint);
//	}

	public Object catcheCall(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		// generate the key under which cached value is stored
		// will look like caching.aspectj.Calculator.sum(Integer=1;Integer=2;)
		StringBuilder keyBuff = new StringBuilder();

		// append name of the class
		keyBuff.append(thisJoinPoint.getTarget().getClass().getName());

		// append name of the method
		keyBuff.append(".").append(thisJoinPoint.getSignature().getName());

		keyBuff.append("(");
		// loop through cacheable method arguments
		for (final Object arg : thisJoinPoint.getArgs()) {
			// append argument type and value
			keyBuff.append(arg.getClass().getSimpleName() + "=" + arg + ";");
		}
		keyBuff.append(")");
		String key = keyBuff.toString();

		log.debug("Key = " + key);
		Object result = cache.get(key);
		if (result == null) {
			log.debug("Result not yet cached. Must be calculated...");
			result = thisJoinPoint.proceed();
			log.info("Storing calculated value '" + result + "' to cache");
			cache.put(key, result);
		} else {
			log.debug("Result '" + result + "' was found in cache");
		}
		return result;
	}

}
