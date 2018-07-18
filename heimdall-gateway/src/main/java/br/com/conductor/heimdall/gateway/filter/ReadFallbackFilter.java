package br.com.conductor.heimdall.gateway.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import br.com.conductor.heimdall.core.entity.Fallback;
import br.com.conductor.heimdall.core.util.Constants;

//@Component
public class ReadFallbackFilter extends ZuulFilter {
	
	@Autowired private RedisTemplate<String, Fallback> redisTemplate;
	@Autowired private HashOperations<String, String, Fallback> hashOperations;
	
	@PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

	@Override
	public boolean shouldFilter() {
		return false;
//		RequestContext ctx = RequestContext.getCurrentContext();
//		return !ctx.containsKey(FORWARD_TO_KEY) // a filter has already forwarded
//				&& !ctx.containsKey(SERVICE_ID_KEY) // a filter has already determined serviceId
//				&& !ctx.containsKey(Constants.INTERRUPT); //has no route to process
	}

	@Override
	public Object run() {
		// verificar se há dados do pattern no redis, caso exista, bloquear request.
		Fallback fallback = hashOperations.get(Fallback.KEY, "");
		return null;
	}

	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 400;
	}

}
