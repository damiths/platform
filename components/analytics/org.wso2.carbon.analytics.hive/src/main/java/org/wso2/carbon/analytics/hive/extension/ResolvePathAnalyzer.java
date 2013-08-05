package org.wso2.carbon.analytics.hive.extension;

/**
 * Copyright (c) 2009, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.hive.annotation.HiveAnnotation;
import org.wso2.carbon.analytics.hive.annotation.util.AnnotationBuilder;

public class ResolvePathAnalyzer extends AbstractHiveAnalyzer {


    private static final Log log = LogFactory.getLog(ResolvePathAnalyzer.class);

    @Override
    public void execute() {
        String path = null;

        HiveAnnotation annotation= AnnotationBuilder.getAnnotation("resolvePath");

        String filePath= annotation.getParameter("path");

        try {
            path = resolvePath(filePath);
        } catch (Exception e) {
            log.error("Couldn't resolve file path", e);
        }
        if (path != null) {
            setProperty("FILE_PATH", path);
        }
    }

    public String resolvePath(String filePath) {
        String resolvedPath;
        String os = System.getProperty("os.name");

        if (os.startsWith("Windows") ) {
            resolvedPath = filePath.replace("/", "\\");
        } else if (os.startsWith("Linux")|| os.startsWith("Mac")) {
            resolvedPath = filePath;
         } else {
            resolvedPath = filePath;
        }
        return resolvedPath;
    }

}
