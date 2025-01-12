/******************************************************************************
*
* Copyright 2020-, UT-Battelle, LLC. All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
*  o Redistributions of source code must retain the above copyright notice, this
*    list of conditions and the following disclaimer.
*    
*  o Redistributions in binary form must reproduce the above copyright notice,
*    this list of conditions and the following disclaimer in the documentation
*    and/or other materials provided with the distribution.
*    
*  o Neither the name of the copyright holder nor the names of its
*    contributors may be used to endorse or promote products derived from
*    this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*******************************************************************************/
package io.bssw.psip;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private Environment env;
	
	public SecurityConfig() {
		/*
		 * Disable Spring security defaults - we are only using it for the HTTP header
		 * configuration
		 */
		super(true);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * General headers used.
		 * 
		 * Referrer policy header depends on whether or not the application is in
		 * production: if so, reveal the origin (but never the path). Otherwise, mask
		 * the origin entirely.
		 * 
		 * Vaadin productionMode will load fonts/styles from Google; for simplicity's
		 * sake, this isn't blocked locally.
		 * 
		 * TODO: try to make the CSP better, but unfortunately using Vaadin may make
		 * this difficult
		 */
		http.headers()
				.contentSecurityPolicy(
						"default-src 'none'; "
								+ "frame-ancestors 'none'; "
								+ "form-action 'none'; "
								+ "base-uri 'self'; "
								+ "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com/; "
								+ "script-src 'self' 'unsafe-inline' 'unsafe-eval' data:; "
								+ "connect-src 'self'; "
								+ "img-src 'self'; "
								+ "frame-src https://www.youtube.com/embed/; "
								+ "font-src 'self' data: https://fonts.gstatic.com/;"
				)
				.and()
				.httpStrictTransportSecurity()
				.and()
				.referrerPolicy((Arrays.asList(env.getActiveProfiles()).contains("prod")) ? ReferrerPolicy.STRICT_ORIGIN
						: ReferrerPolicy.NO_REFERRER)
				.and()
				.featurePolicy(
						"accelerometer 'none'; "
								+ "camera 'none'; "
								+ "fullscreen 'self'; "
								+ "geolocation 'none'; "
								+ "gyroscope 'none'; "
								+ "magnetometer 'none'; "
								+ "microphone 'none'; "
								+ "midi 'none'; "
								+ "payment 'none'; "
								+ "speaker 'none'; "
								+ "sync-xhr 'none'; "
								+ "usb 'none'");

		// permit all requests - Vaadin handles security
		http.authorizeRequests().anyRequest().permitAll()
				.and()
				.anonymous();
		
//		http.requiresChannel().anyRequest().requiresSecure();
	}
}
