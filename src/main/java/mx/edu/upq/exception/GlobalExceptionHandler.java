package mx.edu.upq.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ModelAndView handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", 400);
		mav.addObject("error", "Error de validación");
		mav.addObject("message", ex.getBindingResult().getFieldError().getDefaultMessage());
		mav.addObject("path", request.getRequestURI());
		return mav;
	}

	@ExceptionHandler(InternalException.class)
	public ModelAndView handleInternal(InternalException ex, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", 500);
		mav.addObject("error", "Error interno");
		mav.addObject("message", ex.getMessage());
		mav.addObject("path", request.getRequestURI());
		return mav;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ModelAndView handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
		if (ex.getRequestURL().contains("favicon.ico")) {
			return new ModelAndView(); // Ignorar peticiones de favicon
		}
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", 404);
		mav.addObject("error", "Página no encontrada");
		mav.addObject("message", "La ruta '" + ex.getRequestURL() + "' no existe");
		mav.addObject("path", request.getRequestURI());
		return mav;
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleGeneral(Exception ex, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", 500);
		mav.addObject("error", "Error inesperado");
		mav.addObject("message", ex.getMessage());
		mav.addObject("path", request.getRequestURI());
		return mav;
	}

}
