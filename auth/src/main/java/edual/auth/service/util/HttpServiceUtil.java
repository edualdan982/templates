package edual.auth.service.util;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class HttpServiceUtil implements IHttpServiceUtil {

  @Override
  public HttpServletRequest getContextServerlet() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes) attributes).getRequest();
    } else
      return null;
  }
}