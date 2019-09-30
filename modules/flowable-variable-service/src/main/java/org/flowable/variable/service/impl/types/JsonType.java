/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.variable.service.impl.types;

import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.api.types.VariableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Tijs Rademakers
 */
public class JsonType implements VariableType {

    public static final String TYPE_NAME = "json";

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonType.class);

    protected final int maxLength;
    protected ObjectMapper objectMapper;

    public JsonType(int maxLength, ObjectMapper objectMapper) {
        this.maxLength = maxLength;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public Object getValue(ValueFields valueFields) {
        JsonNode jsonValue = null;
        if (valueFields.getTextValue() != null && valueFields.getTextValue().length() > 0) {
            try {
                jsonValue = objectMapper.readTree(valueFields.getTextValue());
            } catch (Exception e) {
                LOGGER.error("Error reading json variable {}", valueFields.getName(), e);
            }
        }
        return jsonValue;
    }

    @Override
    public void setValue(Object value, ValueFields valueFields) {
        valueFields.setTextValue(value != null ? objectMapper.valueToTree(value).toString() : null);
    }

    @Override
    public boolean isAbleToStore(Object value) {
        if (value == null || value instanceof LinkedHashMap) {
            return true;
        }

        if (JsonNode.class.isAssignableFrom(value.getClass())) {
            JsonNode jsonValue = (JsonNode) value;
            return jsonValue.toString().length() <= maxLength;
        }

        return false;
    }
}
