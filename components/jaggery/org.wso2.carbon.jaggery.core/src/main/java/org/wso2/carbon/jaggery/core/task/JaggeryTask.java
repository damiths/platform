/*
 *  Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.jaggery.core.task;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.wso2.carbon.jaggery.core.internal.JaggeryCoreServiceComponent;
import org.wso2.carbon.jaggery.core.manager.CommonManager;
import org.wso2.carbon.ntask.core.AbstractTask;
import org.wso2.carbon.scriptengine.engine.RhinoEngine;
import org.wso2.carbon.scriptengine.exceptions.ScriptException;

public class JaggeryTask extends AbstractTask {

	private static final Log log = LogFactory.getLog(JaggeryTask.class);
	
	@Override
	public void execute() {

        try {

			Map<String, Object> taskMap = JaggeryCoreServiceComponent.getTaskMap();
			String taskName = getProperties().get(JaggeryTaskConstants.TASK_NAME);

        	if(taskName == null) {
        		return;
        	}
        	
			@SuppressWarnings("unchecked")
			Map<String, Object> taskProperties = (Map<String, Object>) taskMap.get(taskName);
        	
        	if(taskProperties == null) {
        		return;
        	}
        	
        	Object[] parameters = (Object[]) taskProperties.get(JaggeryTaskConstants.FUNCTION_PARAMETERS);
        	Object jsFunction = (Object) taskProperties.get(JaggeryTaskConstants.JAVASCRIPT_FUNCTION);
        	
        	Context context = RhinoEngine.enterContext();
            Object[] args;

            String jaggeryDir = System.getProperty("jaggery.home");
            if (jaggeryDir == null) {
                jaggeryDir = System.getProperty("carbon.home");
            }

            if(jaggeryDir == null) {
                log.error("Unable to find jaggery.home or carbon.home system properties");
                return;
            }
            jaggeryDir = jaggeryDir + File.separator + JaggeryTaskConstants.JAGGERY_MODULES_DIR;
            log.error("test " + jaggeryDir);
            RhinoEngine engine = new CommonManager(jaggeryDir).getEngine();
            ScriptableObject scope = engine.getRuntimeScope();
            if (jsFunction instanceof Function) {
                if (parameters != null) {
                    args = parameters;
                } else {
                    args = new Object[0];
                }
                Function function = (Function) jsFunction;
                function.call(context, scope, scope, args);
            } else if (jsFunction instanceof String) {
                String jsString = (String) jsFunction;
                context.evaluateString(scope, jsString, "Load JavaScriptString", 0, null);
            }
            RhinoEngine.exitContext();
        } catch (ScriptException e) {
            log.error(e.getMessage(), e);
        } 
		
	}

}
