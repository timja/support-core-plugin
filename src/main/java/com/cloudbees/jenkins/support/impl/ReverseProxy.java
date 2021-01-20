package com.cloudbees.jenkins.support.impl;

import com.cloudbees.jenkins.support.api.Component;
import com.cloudbees.jenkins.support.api.Container;
import com.cloudbees.jenkins.support.api.PrintedContent;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Attempts to detect reverse proxies in front of Jenkins.
 */
@Extension
public class ReverseProxy extends Component {

  public enum Trilean {
    TRUE, FALSE, UNKNOWN
  }

  // [RFC 7239, section 4: Forwarded](https://tools.ietf.org/html/rfc7239#section-4) standard header.
  static final String FORWARDED_HEADER = "Forwarded";
  // Non-standard headers.
  static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
  static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";
  static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
  static final String X_FORWARDED_PORT_HEADER = "X-Forwarded-Port";

  private static final Collection<String> FORWARDED_HEADERS = ImmutableList.of(FORWARDED_HEADER, X_FORWARDED_FOR_HEADER,
          X_FORWARDED_PROTO_HEADER, X_FORWARDED_HOST_HEADER, X_FORWARDED_PORT_HEADER);

  @NonNull
  @Override
  public Set<Permission> getRequiredPermissions() {
    return Collections.singleton(Jenkins.ADMINISTER);
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return "Reverse Proxy";
  }

  @Override
  public void addContents(@NonNull Container container) {
    container.add(new PrintedContent("reverse-proxy.md") {
      @Override protected void printTo(PrintWriter out) {
        out.println("Reverse Proxy");
        out.println("=============");
        for (String xForwardedHeader : FORWARDED_HEADERS) {
          out.println(String.format(" * Detected `%s` header: %s", xForwardedHeader, isXForwardedHeaderDetected(xForwardedHeader)));
        }
      }

      @Override
      public boolean shouldBeFiltered() {
        // The information of this content is not sensible, so it doesn't need to be filtered.
        return false;
      }
    });
  }

  private Trilean isXForwardedHeaderDetected(String xForwardedHeader) {
    StaplerRequest req = getCurrentRequest();
    if (req == null) {
      return Trilean.UNKNOWN;
    }
    return req.getHeader(xForwardedHeader) != null ? Trilean.TRUE : Trilean.FALSE;
  }

  protected StaplerRequest getCurrentRequest() {
    return Stapler.getCurrentRequest();
  }
}
