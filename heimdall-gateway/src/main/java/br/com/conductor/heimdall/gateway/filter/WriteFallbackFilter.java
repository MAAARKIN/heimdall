package br.com.conductor.heimdall.gateway.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import br.com.conductor.heimdall.core.entity.Fallback;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.util.Constants;

//@Component
public class WriteFallbackFilter extends ZuulFilter {
	
	@Autowired
    Property property;
	@Autowired
	private RedisTemplate<String, Fallback> redisTemplate;
	@Autowired
	private HashOperations<String, Long, Fallback> hashOperations;

	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public boolean shouldFilter() {
		return false;
//		RequestContext ctx = RequestContext.getCurrentContext();
//		return !ctx.containsKey(FORWARD_TO_KEY) // a filter has already forwarded
//				&& !ctx.containsKey(SERVICE_ID_KEY) // a filter has already determined serviceId
//				&& !ctx.containsKey(Constants.INTERRUPT) //has no route to process
//				&& property.getFallback().getHttpCodeToBlock().contains(ctx.getResponseStatusCode()); //filter response status codes present in properties
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		
		Long operationId = (Long) ctx.get("operationId");
		Fallback fallback = new Fallback();
		
		if (hashOperations.hasKey(Fallback.KEY, operationId)) {
			fallback = hashOperations.get(Fallback.KEY, operationId);
			if (fallback.getAttempts() >= property.getFallback().getAttempts()) {
				
			}
			
//			Duration.between(LocalDateTime.now(), fallback.getLastRequest()).getSeconds() != 0;
		}

		hashOperations.put(Fallback.KEY, operationId, fallback);
		return null;
	}
	
//	private boolean isIntervalEnded(Fallback fallback) {         
//
//        if (fallback.getInterval() == Interval.SECONDS) {
//             return Duration.between(LocalDateTime.now(), rate.getLastRequest()).getSeconds() != 0;
//        } else if (rate.getInterval() == Interval.MINUTES) {
//             return Duration.between(LocalDateTime.now(), rate.getLastRequest()).toMinutes() != 0;
//        } else {
//             return Duration.between(LocalDateTime.now(), rate.getLastRequest()).toHours() != 0;
//        }
//   }

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
