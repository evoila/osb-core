package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class SchemaProperty {

	/*
	 * Written at the 25.05.2018 using the formal specification from
	 * https://cswr.github.io/JsonSchema/spec/grammar/
	 * 
	 * Missing due to Java incompatibly - allow a List of values and a single
	 * value for type at the same time - allow a List of items and a single
	 * value for items at the same time - allow a Boolean and a Schema object
	 * for additionalItems at the same time - allow a Boolean and a Schema
	 * object for additionalProperties at the same time - dependencies in
	 * objects
	 */

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "description", required = false)
	private String description;

	@JsonSerialize
	@JsonProperty("type")
	private String type;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private String title;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Map<String, SchemaProperty> definitions;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "enum", required = false)
	private List<Object> enums;

	// --------------- String Restrictions ---------------

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer minLength;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer maxLength;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private String pattern;

	// --------------- Numeric Restrictions ---------------

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer minimum;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Boolean exclusiveMinimum;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer maximum;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Boolean exclusiveMaximum;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer multipleOf;

	// --------------- Array Restrictions ---------------

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "items", required = false)
	private List<SchemaProperty> items;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private List<SchemaProperty> additionalItems;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer minItems;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer maxItems;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Boolean uniqueItems;

	// --------------- Object Restrictions ---------------

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "properties", required = false)
	private Map<String,SchemaProperty> properties;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Map<String,SchemaProperty> additionalProperties;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private List<String> required;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer minProperties;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Integer maxProperties;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(required = false)
	private Map<String, SchemaProperty> patternProperties;


	public SchemaProperty() {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, SchemaProperty> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Map<String, SchemaProperty> definitions) {
		this.definitions = definitions;
	}

	public List<Object> getEnums() {
		return enums;
	}

	public void setEnums(List<Object> enums) {
		this.enums = enums;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}

	public Boolean getExclusiveMinimum() {
		return exclusiveMinimum;
	}

	public void setExclusiveMinimum(Boolean exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
	}

	public Integer getMaximum() {
		return maximum;
	}

	public void setMaximum(Integer maximum) {
		this.maximum = maximum;
	}

	public Boolean getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	public void setExclusiveMaximum(Boolean exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
	}

	public Integer getMultipleOf() {
		return multipleOf;
	}

	public void setMultipleOf(Integer multipleOf) {
		this.multipleOf = multipleOf;
	}

	public List<SchemaProperty> getItems() {
		return items;
	}

	public void setItems(List<SchemaProperty> items) {
		this.items = items;
	}

	public List<SchemaProperty> getAdditionalItems() {
		return additionalItems;
	}

	public void setAdditionalItems(List<SchemaProperty> additionalItems) {
		this.additionalItems = additionalItems;
	}

	public Integer getMinItems() {
		return minItems;
	}

	public void setMinItems(Integer minItems) {
		this.minItems = minItems;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

	public Boolean getUniqueItems() {
		return uniqueItems;
	}

	public void setUniqueItems(Boolean uniqueItems) {
		this.uniqueItems = uniqueItems;
	}

	public List<String> getRequired() {
		return required;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}

	public Integer getMinProperties() {
		return minProperties;
	}

	public void setMinProperties(Integer minProperties) {
		this.minProperties = minProperties;
	}

	public Integer getMaxProperties() {
		return maxProperties;
	}

	public void setMaxProperties(Integer maxProperties) {
		this.maxProperties = maxProperties;
	}

	public Map<String, SchemaProperty> getPatternProperties() {
		return patternProperties;
	}

	public void setPatternProperties(Map<String, SchemaProperty> patternProperties) {
		this.patternProperties = patternProperties;
	}

	public Map<String, SchemaProperty> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, SchemaProperty> properties) {
		this.properties = properties;
	}

	public Map<String, SchemaProperty> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, SchemaProperty> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}
