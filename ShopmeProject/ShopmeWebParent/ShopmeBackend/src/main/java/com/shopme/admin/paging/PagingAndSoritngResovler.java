package com.shopme.admin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSoritngResovler implements HandlerMethodArgumentResolver {

	@Override
	@Nullable
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model, NativeWebRequest request,
			 WebDataBinderFactory binderFactory) throws Exception {
		
	
		
		PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);	

		String sortDir = request.getParameter("sortDir");
		String sortField = request.getParameter("sortField");
		String keyword = request.getParameter("keyword");
		model.addAttribute("moduleURL", annotation.moduleURL());
		
	

		
		return new PagingAndSortingHelper(model,annotation.listName(), annotation.moduleURL(),sortField, sortDir, keyword);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
	
		return  parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
	}

}
