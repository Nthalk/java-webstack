package com.nthalk.light.websocket.resources;

import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.*;
import org.atmosphere.handler.AtmosphereHandlerAdapter;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.atmosphere.util.SimpleBroadcaster;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

@AtmosphereHandlerService(
    interceptors = {
        AtmosphereResourceLifecycleInterceptor.class,
        TrackMessageSizeInterceptor.class,
        BroadcastOnPostAtmosphereInterceptor.class,
        SuspendTrackerInterceptor.class},
    broadcaster = SimpleBroadcaster.class)
public class WebSocketResource extends AtmosphereHandlerAdapter implements AtmosphereServletProcessor {
  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WebSocketResource.class);
  private ApplicationContext context;
  private Broadcaster broadcaster;

  @Override
  public void onRequest(AtmosphereResource resource) throws IOException {
    final AtmosphereRequest req = resource.getRequest();
    final Broadcaster broadcaster = getBroadcaster(req);
    resource.setBroadcaster(broadcaster);
    if (req.getMethod().equalsIgnoreCase("GET")) {
      resource.suspend();
    }
  }

  @Override
  public void onStateChange(AtmosphereResourceEvent event) throws IOException {
    AtmosphereResource r = event.getResource();
    AtmosphereResponse res = r.getResponse();
    if (r.isSuspended() && event.getMessage() != null) {
      String body = event.getMessage().toString();
      res.getWriter().write(body);
      switch (r.transport()) {
        case JSONP:
        case LONG_POLLING:
          event.getResource().resume();
          break;
        case WEBSOCKET:
        case STREAMING:
          res.getWriter().flush();
          break;
      }
    } else if (!event.isResuming()) {
      event.broadcaster().broadcast(event.getMessage());
    }
  }


  @Override
  public void init(AtmosphereConfig atmosphereConfig) throws ServletException {
    context = getWebApplicationContext(atmosphereConfig.getServletContext());
    broadcaster = new SimpleBroadcaster().initialize("broadcaster", atmosphereConfig);
  }


  private Broadcaster getBroadcaster(AtmosphereRequest request) {
    return broadcaster;
  }
}
