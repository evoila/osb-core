package de.evoila.cf.broker.model.json.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;

import java.util.*;

/**
 * The type wraps the json schema specification at :
 * <a href="http://tools.ietf.org/id/draft-zyp-json-schema-03.txt"> Json JsonSchema
 * Draft </a> <blockquote> JSON (JavaScript Object Notation) JsonSchema defines the
 * media type "application/schema+json", a JSON based format for defining the
 * structure of JSON data. JSON JsonSchema provides a contract for what JSON data is
 * required for a given application and how to interact with it. JSON JsonSchema is
 * intended to define validation, documentation, hyperlink navigation, and
 * interaction control of JSON data. </blockquote>
 * 
 * <blockquote> JSON (JavaScript Object Notation) JsonSchema is a JSON media type
 * for defining the structure of JSON data. JSON JsonSchema provides a contract for
 * what JSON data is required for a given application and how to interact with
 * it. JSON JsonSchema is intended to define validation, documentation, hyperlink
 * navigation, and interaction control of JSON data. </blockquote>
 * 
 * An example JSON JsonSchema provided by the JsonSchema draft:
 * 
 * <pre>
 * 	{
 * 	  "name":"Product",
 * 	  "properties":{
 * 	    "id":{
 * 	      "type":"number",
 * 	      "description":"Product identifier",
 * 	      "required":true
 * 	    },
 * 	    "name":{
 * 	      "description":"Name of the product",
 * 	      "type":"string",
 * 	      "required":true
 * 	    },
 * 	    "price":{
 * 	      "required":true,
 * 	      "type": "number",
 * 	      "minimum":0,
 * 	      "required":true
 * 	    },
 * 	    "tags":{
 * 	      "type":"array",
 * 	      "items":{
 * 	        "type":"string"
 * 	      }
 * 	    }
 * 	  },
 * 	  "links":[
 * 	    {
 * 	      "rel":"full",
 * 	      "href":"{id}"
 * 	    },
 * 	    {
 * 	      "rel":"comments",
 * 	      "href":"comments/?id={id}"
 * 	    }
 * 	  ]
 * 	}
 * </pre>
 * current Definition: https://json-schema.org/understanding-json-schema/reference/index.html
 * @author Johannes Hiemer.
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class JsonSchema {

    /**
     * This attribute defines the current URI of this schema (this attribute is
     * effectively a "self" link). This URI MAY be relative or absolute. If the
     * URI is relative it is resolved against the current URI of the parent
     * schema it is contained in. If this schema is not contained in any parent
     * schema, the current URI of the parent schema is held to be the URI under
     * which this schema was addressed. If id is missing, the current URI of a
     * schema is defined to be that of the parent schema. The current URI of the
     * schema is also used to construct relative references such as for $ref.
     */
    @JsonProperty
    private String id;

    /**
	 * This attribute defines a URI of a schema that contains the full
	 * representation of this schema. When a validator encounters this
	 * attribute, it SHOULD replace the current schema with the schema
	 * referenced by the value's URI (if known and available) and re- validate
	 * the instance. This URI MAY be relative or absolute, and relative URIs
	 * SHOULD be resolved against the URI of the current schema.
	 */
	@JsonProperty("$ref")
	private String ref;

	/**
	 * This attribute defines a URI of a JSON JsonSchema that is the schema of the
	 * current schema. When this attribute is defined, a validator SHOULD use
	 * the schema referenced by the value's URI (if known and available) when
	 * resolving Hyper JsonSchema (Section 6) links (Section 6.1).
	 * 
	 * A validator MAY use this attribute's value to determine which version of
	 * JSON JsonSchema the current schema is written in, and provide the appropriate
	 * validation features and behavior. Therefore, it is RECOMMENDED that all
	 * schema authors include this attribute in their schemas to prevent
	 * conflicts with future JSON JsonSchema specification changes.
	 */
	@JsonProperty("$schema")
	private String schema;

	/**
	 * This attribute takes the same values as the "type" attribute, however if
	 * the instance matches the type or if this value is an array and the
	 * instance matches any type or schema in the array, then this instance is
	 * not valid.
	 */
	@JsonProperty
	private JsonSchema[] disallow;

    /**
     * This attribute indicates if the instance is not modifiable.
     * This is false by default, making the instance modifiable.
     */
    @JsonProperty
    private Boolean readonly = null;

    /**
     * This attribute is a string that provides a full description of the of
     * purpose the instance property.
     */
    private String description;

    private JsonFormatTypes type;

    // SimpleSchema Section
    /**
     * This attribute is a string that provides a short description of the
     * instance property.
     */
    protected String title;

    /**
     * This attribute is an object, which contains a possible default value;
     */
    @JsonProperty("default")
    private Object defaults;

    /**
     * This attribute is a string that provides a links related to description of the
     * instance property.
     */
    protected LinkDescriptionObject[] links;

    // ContainerSchema Section
    /**
     * This provides an enumeration of all possible values that are valid
     for the instance property.  This MUST be an array, and each item in
     the array represents a possible value for the instance value.  If
     this attribute is defined, the instance value MUST be one of the
     values in the array in order for the schema to be valid.  Comparison
     of enum values uses the same algorithm as defined in "uniqueItems"
     (Section 5.15).
     */
    @JsonProperty(value = "enum", required = true)
    protected Set<String> enums;

    /**
     * This provides an enumeration of all possible values that are valid
     for the instance property.  This MUST be an array, and each item in
     the array represents a possible value for the instance value.  If
     this attribute is defined, the instance value MUST be one of the
     values in the array in order for the schema to be valid.  Comparison
     of enum values uses the same algorithm as defined in "uniqueItems"
     (Section 5.15).
     */
    @JsonProperty(value = "oneOf", required = true)
    protected Set<JsonSchema> oneOf;

    // ArraySchema Section
    /**
     **/
    @JsonProperty
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(with = { JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED })
    protected List<JsonSchema> additionalItems;

    /**
     */
    @JsonProperty
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(with = { JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED })
    protected List<JsonSchema> items;

    /**
     * This attribute defines the maximum number of values in an array
     **/
    @JsonProperty
    protected Integer maxItems;

    /**
     * This attribute defines the minimum number of values in an array
     **/
    @JsonProperty
    protected Integer minItems;

    /**
     * This attribute indicates that all items in an array instance MUST be
     unique (contains no two identical values).

     Two instance are consider equal if they are both of the same type
     and:

     are null; or are booleans/numbers/strings and have the same value; or

     are arrays, contains the same number of items, and each item in
     the array is equal to the corresponding item in the other array;
     or

     are objects, contains the same property names, and each property
     in the object is equal to the corresponding property in the other
     object.
     **/
    @JsonProperty
    protected Boolean uniqueItems;

    // NumberSchema
    /**
     * This attribute indicates if the value of the instance (if the
     instance is a number) can not equal the number defined by the
     "maximum" attribute.
     */
    @JsonProperty
    private Boolean exclusiveMaximum;

    /**
     * This attribute indicates if the value of the instance (if the
     instance is a number) can not equal the number defined by the
     "minimum" attribute.
     */
    @JsonProperty
    private Boolean exclusiveMinimum;

    /**This attribute defines the maximum value of the instance property*/
    @JsonProperty
    private Double maximum = null;

    /**This attribute defines the minimum value of the instance property*/
    @JsonProperty
    private Double minimum = null;

    /** The value of the instance needs to be a multiple of this attribute */
    @JsonProperty
    private Double multipleOf = null;

    // IntegerSchema Section
    /**
     * This attribute defines what value the number instance must be
     divisible by with no remainder (the result of the division must be an
     integer.)  The value of this attribute SHOULD NOT be 0.
     */
    private Integer divisibleBy;

    // ObjectSchema Section
    /**
     * This attribute indicates if the instance must have a value, and not be
     * undefined. This is false by default, making the instance optional.
     */
    @JsonProperty
    private List<String> required;

    @JsonProperty
    private Integer minProperties;

    @JsonProperty
    private Integer maxProperties;

    /**
     * This attribute is an object that defines the requirements of a property
     * on an instance object. If an object instance has a property with the same
     * name as a property in this attribute's object, then the instance must be
     * valid against the attribute's property value
     */
    @JsonProperty
    private Map<String, JsonSchema> dependencies;

    /**
     *
     This attribute is an object that defines the jsonSchema for a set of property
     * names of an object instance. The name of each property of this
     * attribute's object is a regular expression pattern in the ECMA 262/Perl 5
     * format, while the value is a jsonSchema. If the pattern matches the name of a
     * property on the instance object, the value of the instance's property
     * MUST be valid against the pattern name's jsonSchema value.
     */
    @JsonProperty
    private Map<String, Object> patternProperties;

    /**
     * This attribute is an object with property definitions that define the
     * valid values of instance object property values. When the instance value
     * is an object, the property values of the instance object MUST conform to
     * the property definitions in this object. In this object, each property
     * definition's value MUST be a jsonSchema, and the property's name MUST be the
     * name of the instance property that it defines. The instance property
     * value MUST be valid according to the jsonSchema from the property definition.
     * Properties are considered unordered, the order of the instance properties
     * MAY be in any order.
     */
    @JsonProperty
    private Map<String, JsonSchema> properties;

    // StringSchema Section
    /** this defines the maximum length of the string. */
    @JsonProperty
    private Integer maxLength;

    /** this defines the minimum length of the string. */
    @JsonProperty
    private Integer minLength;

    /**
     * this provides a regular expression that a string instance MUST match in
     * order to be valid. Regular expressions SHOULD follow the regular
     * expression specification from ECMA 262/Perl 5
     */
    @JsonProperty
    private String pattern;

    // ValueSchema Section
    @JsonProperty
    private Map<String, JsonSchema> definitions;

    /**
     * This property defines the type of data, content type, or microformat to
     * be expected in the instance property values. A format attribute MAY be
     * one of the values listed below, and if so, SHOULD adhere to the semantics
     * describing for the format. A format SHOULD only be used to give meaning
     * to primitive types (string, integer, number, or boolean). Validators MAY
     * (but are not required to) validate that the instance values conform to a
     * format.
     *
     * Additional custom formats MAY be created. These custom formats MAY be
     * expressed as an URI, and this URI MAY reference a schema of that
     *<p>
     * NOTE: serialization of `format` was fixed in Jackson 2.7; requires at least
     * this version of databind
     */
    @JsonProperty
    protected JsonValueFormat format;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public JsonSchema[] getDisallow() {
        return disallow;
    }

    public void setDisallow(JsonSchema[] disallow) {
        this.disallow = disallow;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JsonFormatTypes getType() {
        return type;
    }

    public void setType(JsonFormatTypes type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public Object getDefault() {
        return defaults;
    }

    @JsonProperty
    public void setDefault(Object defaults) {
        this.defaults = defaults;
    }

    public LinkDescriptionObject[] getLinks() {
        return links;
    }

    public void setLinks(LinkDescriptionObject[] links) {
        this.links = links;
    }

    public Set<String> getEnums() {
        return enums;
    }

    public void setEnums(Set<String> enums) {
        this.enums = enums;
    }

    public Set<JsonSchema> getOneOf() {
        return oneOf;
    }

    public void setOneOf(Set<JsonSchema> oneOf) {
        this.oneOf = oneOf;
    }

    public List<JsonSchema> getAdditionalItems() {
        return additionalItems;
    }

    public void setAdditionalItems(List<JsonSchema> additionalItems) {
        this.additionalItems = additionalItems;
    }

    public List<JsonSchema> getItems() {
        return items;
    }

    public void setItems(List<JsonSchema> items) {
        this.items = items;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public void setUniqueItems(Boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    public Boolean getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    public void setExclusiveMaximum(Boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public Boolean getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public void setExclusiveMinimum(Boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public Double getMaximum() {
        return maximum;
    }

    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public Double getMultipleOf() {
        return multipleOf;
    }

    public void setMultipleOf(Double multipleOf) {
        this.multipleOf = multipleOf;
    }

    public Integer getDivisibleBy() {
        return divisibleBy;
    }

    public void setDivisibleBy(Integer divisibleBy) {
        this.divisibleBy = divisibleBy;
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

    public Map<String, JsonSchema> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, JsonSchema> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, Object> getPatternProperties() {
        return patternProperties;
    }

    public void setPatternProperties(Map<String, Object> patternProperties) {
        this.patternProperties = patternProperties;
    }

    public Map<String, JsonSchema> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, JsonSchema> properties) {
        this.properties = properties;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Map<String, JsonSchema> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, JsonSchema> definitions) {
        this.definitions = definitions;
    }

    public JsonValueFormat getFormat() {
        return format;
    }

    public void setFormat(JsonValueFormat format) {
        this.format = format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonSchema that = (JsonSchema) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(ref, that.ref) &&
                Objects.equals(schema, that.schema) &&
                Arrays.equals(disallow, that.disallow) &&
                Objects.equals(readonly, that.readonly) &&
                Objects.equals(description, that.description) &&
                type == that.type &&
                Objects.equals(title, that.title) &&
                Arrays.equals(links, that.links) &&
                Objects.equals(enums, that.enums) &&
                Objects.equals(oneOf, that.oneOf) &&
                Objects.equals(additionalItems, that.additionalItems) &&
                Objects.equals(items, that.items) &&
                Objects.equals(maxItems, that.maxItems) &&
                Objects.equals(minItems, that.minItems) &&
                Objects.equals(uniqueItems, that.uniqueItems) &&
                Objects.equals(exclusiveMaximum, that.exclusiveMaximum) &&
                Objects.equals(exclusiveMinimum, that.exclusiveMinimum) &&
                Objects.equals(maximum, that.maximum) &&
                Objects.equals(minimum, that.minimum) &&
                Objects.equals(multipleOf, that.multipleOf) &&
                Objects.equals(divisibleBy, that.divisibleBy) &&
                Objects.equals(required, that.required) &&
                Objects.equals(minProperties, that.minProperties) &&
                Objects.equals(maxProperties, that.maxProperties) &&
                Objects.equals(dependencies, that.dependencies) &&
                Objects.equals(patternProperties, that.patternProperties) &&
                Objects.equals(properties, that.properties) &&
                Objects.equals(maxLength, that.maxLength) &&
                Objects.equals(minLength, that.minLength) &&
                Objects.equals(pattern, that.pattern) &&
                Objects.equals(definitions, that.definitions) &&
                format == that.format;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, ref, schema, readonly, description, type, title, enums,
                oneOf, additionalItems, items, maxItems, minItems, uniqueItems, exclusiveMaximum,
                exclusiveMinimum, maximum, minimum, multipleOf, divisibleBy, required, minProperties,
                maxProperties, dependencies, patternProperties, properties, maxLength, minLength,
                pattern, definitions, format);
        result = 31 * result + Arrays.hashCode(disallow);
        result = 31 * result + Arrays.hashCode(links);
        return result;
    }
}