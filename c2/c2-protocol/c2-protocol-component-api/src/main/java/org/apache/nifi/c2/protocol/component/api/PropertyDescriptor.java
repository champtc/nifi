/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.c2.protocol.component.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.nifi.expression.ExpressionLanguageScope;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


@ApiModel
public class PropertyDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String displayName;
    private String description;
    private List<PropertyAllowableValue> allowableValues;
    private String defaultValue;
    private boolean required;
    private boolean sensitive;
    private ExpressionLanguageScope expressionLanguageScope = ExpressionLanguageScope.NONE;
    private String expressionLanguageScopeDescription = ExpressionLanguageScope.NONE.getDescription();
    private DefinedType typeProvidedByValue;
    private String validRegex;
    private String validator;
    private boolean dynamic;
    private PropertyResourceDefinition resourceDefinition;
    private List<PropertyDependency> dependencies;

    @ApiModelProperty(value = "The name of the property key", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty("The display name of the property key, if different from the name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @ApiModelProperty("The description of what the property does")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty("A list of the allowable values for the property")
    public List<PropertyAllowableValue> getAllowableValues() {
        return (allowableValues != null ? Collections.unmodifiableList(allowableValues) : null);
    }

    public void setAllowableValues(List<PropertyAllowableValue> allowableValues) {
        this.allowableValues = allowableValues;
    }

    @ApiModelProperty("The default value if a user-set value is not specified")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @ApiModelProperty("Whether or not  the property is required for the component")
    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @ApiModelProperty("Whether or not  the value of the property is considered sensitive (e.g., passwords and keys)")
    public boolean getSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    @ApiModelProperty("The scope of expression language supported by this property")
    public ExpressionLanguageScope getExpressionLanguageScope() {
        return expressionLanguageScope;
    }

    public void setExpressionLanguageScope(ExpressionLanguageScope expressionLanguageScope) {
        this.expressionLanguageScope = expressionLanguageScope;
        this.expressionLanguageScopeDescription = expressionLanguageScope == null ? null : expressionLanguageScope.getDescription();
    }

    @ApiModelProperty(value = "The description of the expression language scope supported by this property", readOnly = true)
    public String getExpressionLanguageScopeDescription() {
        return expressionLanguageScope == null ? null : expressionLanguageScope.getDescription();
    }

    @ApiModelProperty("Indicates that this property is for selecting a controller service of the specified type")
    public DefinedType getTypeProvidedByValue() {
        return typeProvidedByValue;
    }

    public void setTypeProvidedByValue(DefinedType typeProvidedByValue) {
        this.typeProvidedByValue = typeProvidedByValue;
    }

    @ApiModelProperty("A regular expression that can be used to validate the value of this property")
    public String getValidRegex() {
        return validRegex;
    }

    public void setValidRegex(String validRegex) {
        this.validRegex = validRegex;
    }

    @ApiModelProperty("Name of the validator used for this property descriptor")
    public String getValidator() {
        return validator;
    }

    public void setValidator(final String validator) {
        this.validator = validator;
    }

    @ApiModelProperty("Whether or not the descriptor is for a dynamically added property")
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @ApiModelProperty("Indicates that this property references external resources")
    public PropertyResourceDefinition getResourceDefinition() {
        return resourceDefinition;
    }

    public void setResourceDefinition(PropertyResourceDefinition resourceDefinition) {
        this.resourceDefinition = resourceDefinition;
    }

    @ApiModelProperty("The dependencies that this property has on other properties")
    public List<PropertyDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<PropertyDependency> dependencies) {
        this.dependencies = dependencies;
    }
}
