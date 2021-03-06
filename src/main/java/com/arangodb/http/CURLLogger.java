/*
 * Copyright (C) 2012,2013 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arangodb.http;

import java.util.Map.Entry;

import org.apache.http.auth.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.http.HttpRequestEntity.RequestType;
import com.arangodb.util.StringUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @since 1.4.1
 */
public class CURLLogger {

	private static Logger logger = LoggerFactory.getLogger(CURLLogger.class);

	private CURLLogger() {
		// this is a helper class
	}

	public static void log(String url, HttpRequestEntity requestEntity, Credentials credencials) {

		boolean includeBody = (requestEntity.type == RequestType.POST || requestEntity.type == RequestType.PUT
				|| requestEntity.type == RequestType.PATCH) && StringUtils.isNotEmpty(requestEntity.bodyText);

		StringBuilder buffer = new StringBuilder();

		if (includeBody) {
			buffer.append("\n");
			buffer.append("cat <<-___EOB___ | ");
		}

		buffer.append("curl -X ").append(requestEntity.type);
		buffer.append(" --dump -");

		// header
		if (requestEntity.hasHeaders()) {
			for (Entry<String, Object> header : requestEntity.headers.entrySet()) {
				buffer.append(" -H '").append(header.getKey()).append(":").append(header.getValue()).append("'");
			}
		}

		// basic auth
		if (credencials != null) {
			buffer.append(" -u ").append(credencials.getUserPrincipal().getName()).append(":")
					.append(credencials.getPassword());
		}

		if (includeBody) {
			buffer.append(" -d @-");
		}

		buffer.append(" '").append(url).append("'");

		if (includeBody) {
			buffer.append("\n");
			buffer.append(requestEntity.bodyText);
			buffer.append("\n");
			buffer.append("___EOB___");
		}

		logger.debug("[CURL]{}", buffer);

	}

}
