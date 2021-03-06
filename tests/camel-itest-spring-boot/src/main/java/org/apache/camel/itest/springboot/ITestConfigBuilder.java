/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.itest.springboot;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 * Builder for the {@code ITestConfig} that enforces defaults values.
 */
public class ITestConfigBuilder {

    private static final String PROPERTIES_FILE = "/spring-boot-itest.properties";

    private Properties properties;

    private ITestConfig config;

    public ITestConfigBuilder() {
        this.config = new ITestConfig();
    }

    public ITestConfigBuilder(ITestConfig config) {
        this.config = config;
    }

    public ITestConfigBuilder module(String module) {
        config.setModuleName(module);
        return this;
    }

    public ITestConfigBuilder mavenGroup(String mavenGroup) {
        config.setMavenGroup(mavenGroup);
        return this;
    }

    public ITestConfigBuilder mavenVersion(String mavenVersion) {
        config.setMavenVersion(mavenVersion);
        return this;
    }

    public ITestConfigBuilder modulesPath(String path) {
        config.setModulesPath(path);
        return this;
    }

    public ITestConfigBuilder unitTestExpectedNumber(int number) {
        config.setUnitTestsExpectedNumber(number);
        return this;
    }

    public ITestConfigBuilder unitTestBasePackage(String pack) {
        config.setUnitTestBasePackage(pack);
        return this;
    }

    public ITestConfigBuilder unitTestInclusionPattern(String pattern) {
        config.setUnitTestInclusionPattern(pattern);
        return this;
    }

    public ITestConfigBuilder unitTestExclusionPattern(String pattern) {
        config.setUnitTestExclusionPattern(pattern);
        return this;
    }

    public ITestConfigBuilder autostart(Boolean autostart) {
        config.setAutoStartComponent(autostart);
        return this;
    }

    public ITestConfigBuilder resource(String file, String dest) {
        if (config.getResources() == null) {
            config.setResources(new HashMap<>());
        }
        config.getResources().put(file, dest);
        return this;
    }

    public ITestConfigBuilder dependency(String dependencyCanonicalForm) {
        if (config.getAdditionalDependencies() == null) {
            config.setAdditionalDependencies(new HashSet<>());
        }
        config.getAdditionalDependencies().add(dependencyCanonicalForm);
        return this;
    }

    public ITestConfigBuilder resource(String file) {
        return resource(file, file);
    }

    public ITestConfig build() {

        // Checking conditions
        if (config.getModuleName() == null) {
            fail("ModuleName is required");
        }

        // Set the defaults
        if (config.getUnitTestEnabled() == null) {
            config.setUnitTestEnabled(booleanPropertyOr("unitTestEnabled", false));
        }

        if (config.getMavenGroup() == null) {
            config.setMavenGroup(propertyOr("mavenGroup", "org.apache.camel"));
        }

        if (config.getMavenVersion() == null) {
            config.setMavenVersion(propertyOr("mavenVersion", null));
        }

        if (config.getUnitTestInclusionPattern() == null) {
            config.setUnitTestInclusionPattern(propertyOr("unitTestInclusionPattern", "^.*Test$")); // All tests
        }

        if (config.getUnitTestExclusionPattern() == null) {
            config.setUnitTestExclusionPattern(propertyOr("unitTestExclusionPattern", ".*(\\.integration\\..*|XXXTest$)")); // Integration test
        }

        if (config.getIncludeTestDependencies() == null) {
            config.setIncludeTestDependencies(booleanPropertyOr("includeTestDependencies", config.getUnitTestEnabled()));
        }

        if (config.getIncludeProvidedDependencies() == null) {
            config.setIncludeProvidedDependencies(booleanPropertyOr("includeProvidedDependencies", true));
        }

        if (config.getModulesPath() == null) {
            config.setModulesPath(propertyOr("modulesPath", "../../components/"));
        }

        if (config.getUnitTestBasePackage() == null) {
            config.setUnitTestBasePackage(propertyOr("unitTestBasePackage", "org.apache.camel"));
        }

        if (config.getAutoStartComponent() == null) {
            config.setAutoStartComponent(booleanPropertyOr("autostartComponent", true));
        }

        if (config.getResources() == null) {
            config.setResources(Collections.emptyMap());
        }

        if (config.getAdditionalDependencies() == null) {
            config.setAdditionalDependencies(Collections.emptySet());
        }

        return config;
    }

    private void fail(String msg) {
        throw new IllegalStateException("Configuration is not complete: " + msg);
    }

    private String propertyOr(String name, String defaultVal) {
        if (properties == null) {
            properties = new Properties();
            try {
                InputStream in = getClass().getResourceAsStream(PROPERTIES_FILE);
                properties.load(in);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load property file: " + PROPERTIES_FILE, e);
            }
        }

        String res = properties.getProperty(name);
        if (res == null) {
            res = defaultVal;
        }
        return res;
    }

    private Boolean booleanPropertyOr(String name, Boolean defaultVal) {
        String prop = propertyOr(name, null);
        Boolean res = defaultVal;
        if (prop != null) {
            res = Boolean.valueOf(prop);
        }

        return res;
    }
}
