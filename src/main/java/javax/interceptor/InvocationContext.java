/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javax.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>Exposes contextual information about the intercepted invocation and
 * operations that enable interceptor methods to control the behavior 
 * of the invocation chain.</p>
 * 
 * <pre>
 *
 *    &#064;AroundInvoke
 *    public Object logInvocation(InvocationContext ctx) throws Exception {
 *       String class = ctx.getMethod().getDeclaringClass().getName();
 *       String method = ctx.getMethod().getName();
 *       Logger.global.entering(class, method, ctx.getParameters());
 *       try {
 *          Object result = ctx.proceed();
 *          Logger.global.exiting(class, method, result);
 *          return result;
 *       }
 *       catch (Exception e) {
 *          Logger.global.throwing(class, method, e);
 *          throw e;
 *       }
 *
 *    }
 * 
 * </pre>
 *
 * @since Interceptors 1.0
 */
public interface InvocationContext {

    /**
     * Returns the target instance. For {@link AroundConstruct} lifecycle callback 
     * interceptor methods, the <tt>getTarget</tt> method returns <code>null</code> 
     * if called before the {@link #proceed} method.
     * 
     * @return the target instance
     */
    public Object getTarget();

    /**
     * Returns the timer object associated with a timeout
     * method invocation on the target class, or a null value for 
     * interceptor method types other than {@link AroundTimeout}.
     * For example, when associated with an EJB component timeout, this 
     * method returns {@link javax.ejb.Timer}.
     * 
     * @return the timer object or a null value
     *
     * @since Interceptors 1.1
     */
    public Object getTimer();

    /**
     * Returns the method of the target class for which the interceptor
     * was invoked. Returns null in a lifecycle callback interceptor 
     * for which there is no  corresponding lifecycle callback method 
     * declared in the target class (or inherited from a superclass) 
     * or in an {@link AroundConstruct} lifecycle callback interceptor method.
     * 
     * @return the method, or a null value
     */
    public Method getMethod();

    /**
     * Returns the constructor of the target class for which the 
     * {@link AroundConstruct} interceptor method was invoked.  
     * Returns null for interceptor method types other than
     * {@link AroundConstruct} interceptor methods.
     * 
     * @return the constructor, or a null value
     */
    public Constructor<?> getConstructor();

    /**
     * Returns the parameter values that will be passed to the method or
     * constructor of the target class. If {@link #setParameters} has been 
     * called, <code>getParameters</code> returns the values to which the parameters 
     * have been set.  
     * 
     * @return the parameter values, as an array
     * 
     * @exception java.lang.IllegalStateException if invoked within
     * a lifecycle callback method that is not an {@link AroundConstruct} callback.
     */
    public Object[] getParameters();
    
    /**
     * Sets the parameter values that will be passed to the method  or
     * constructor of the target class.  
     *
     * @exception java.lang.IllegalStateException if invoked within
     * a lifecycle callback method that is not an {@link AroundConstruct} callback.
     *
     * @exception java.lang.IllegalArgumentException if the types of the 
     * given parameter values do not match the types of the method or constructor
     * parameters, or if the number of parameters supplied does not equal the 
     * number of method or constructor parameters (if the last parameter is a 
     * vararg parameter of type <code>T</code>, it is considered to be equivalent 
     * to a parameter of type <code>T[]</code>).
     * 
     * @param params the parameter values, as an array
     */
    public void setParameters(Object[] params);

    /**
     * Enables an interceptor to retrieve or update the data associated with 
     * the invocation by another interceptor, business method, and/or 
     * webservices endpoint in the invocation chain.  If interceptors 
     * are invoked as a result of the invocation on a web service endpoint, 
     * the returned value will be an instance of <code>javax.xml.rpc.handler.MessageContext</code>.
     * 
     * @return the context data associated with this invocation or
     * lifecycle callback.  If there is no context data, an
     * empty {@code Map<String,Object>} object will be returned.
     * 
     * @return the context data, as a map
     */
    public Map<String, Object> getContextData();

    /**
     * Proceed to the next interceptor in the interceptor chain. For  
     * around-invoke or around-timeout interceptor methods, the invocation of
     * {@code proceed} in the  last interceptor method in the chain causes 
     * the invocation of the target class method. For {@link AroundConstruct} 
     * lifecycle callback interceptor methods, the invocation of 
     * {@code proceed} in the last interceptor method in the chain causes 
     * the target instance to be created. For all other lifecycle callback 
     * interceptor methods, if there is no callback method defined on the 
     * target class, the invocation of proceed in the last interceptor method 
     * in the chain is a no-op.
     *
     * <p>Return the result of the next method invoked, or a null 
     * value if the method has return type void.
     *
     * <p>
     * 
     * @return the return value of the next method in the chain
     *
     * @exception Exception if thrown by target method or interceptor method in call stack
     */
    public Object proceed() throws Exception;

} 
