package com.codevscode.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

	@Autowired
	private ErrorAttributes errorAttributes;
	
	@RequestMapping("/error")
	public String error(Model model, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> attributes = errorAttributes.getErrorAttributes(new ServletRequestAttributes(request), true);
		System.out.println(attributes);
		model.addAllAttributes(attributes);
		return "error";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

}
