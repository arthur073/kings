/*
 * 
 * Copyright 2005 Sun Microsystems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */


package com.sun.jini.resource;

/**
 * Error thrown when something goes wrong while looking up service providers.
 * In particular, this error will be thrown in the following situations:
 *
 *   <ul>
 *   <li> A concrete provider class cannot be found,
 *   <li> A concrete provider class cannot be instantiated,
 *   <li> The format of a provider-configuration file is illegal, or
 *   <li> An IOException occurs while reading a provider-configuration file.
 *   </ul>
 *
 * @author Sun Microsystems, Inc.
 * 
 * @since 2.0
 */

public class ServiceConfigurationError extends Error {
    private static final long serialVersionUID = 5383327130135226968L;

    /**
     * Constructs a new instance with the specified detail string.
     */
    public ServiceConfigurationError(String msg) {
	super(msg);
    }

    /**
     * Constructs a new instance that wraps the specified throwable.
     */
    public ServiceConfigurationError(Throwable x) {
	super(x);
    }

}
