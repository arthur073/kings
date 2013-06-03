/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* @test 
 * 
 * @summary test ConnectionManager.newRequest handling of mux saturation
 * 
 * @run main/othervm Saturation
 */

import java.io.IOException;
import net.jini.core.constraint.InvocationConstraints;
import net.jini.jeri.Endpoint;
import net.jini.jeri.InboundRequest;
import net.jini.jeri.RequestDispatcher;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.ServerEndpoint.ListenCookie;
import net.jini.jeri.ServerEndpoint.ListenContext;
import net.jini.jeri.ServerEndpoint.ListenEndpoint;
import net.jini.jeri.tcp.TcpServerEndpoint;

public class Saturation {
    private static class Dispatcher implements RequestDispatcher {
	public void dispatch(InboundRequest request) {
	    try {
		Thread.sleep(5000);
	    } catch (InterruptedException e) {
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	ServerEndpoint sep = TcpServerEndpoint.getInstance(0);
	Endpoint ep = sep.enumerateListenEndpoints(new ListenContext() {
	    public ListenCookie addListenEndpoint(ListenEndpoint lep)
		throws IOException
	    {
		return lep.listen(new Dispatcher()).getCookie();
	    }
	});
	for (int i = 0; i < 300; i++) {
	    ep.newRequest(InvocationConstraints.EMPTY).next();
	}
    }
}
