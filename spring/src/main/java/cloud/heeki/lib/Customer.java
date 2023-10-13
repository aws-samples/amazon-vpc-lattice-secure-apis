package cloud.heeki.lib;

import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Customer {
    public UUID uuid;
    public String given_name;
    public String family_name;
    public String birthdate;
    public String email;
    public String phone_number;
    public boolean phone_number_verified;

    public Customer(String given_name, String family_name, String birthdate, String email, String phone_number, boolean phone_number_verified) {
        this.uuid = UUID.randomUUID();
        this.given_name = given_name;
        this.family_name = family_name;
        this.birthdate = birthdate;
        this.email = email;
        this.phone_number = phone_number;
        this.phone_number_verified = phone_number_verified;
    }

    public Customer(String json) {
        System.out.format("Customer(String): %s\n", json);
        Gson g = new Gson();
        Customer c = g.fromJson(json, Customer.class);
        this.uuid = c.uuid != null ? c.uuid : UUID.randomUUID();
        this.given_name = c.given_name;
        this.family_name = c.family_name;
        this.birthdate = c.birthdate;
        this.email = c.email;
        this.phone_number = c.phone_number;
        this.phone_number_verified = c.phone_number_verified;
    }

    public Customer(Map<String, AttributeValue> item) {
        System.out.format("Customer(Map): %s\n", item.get("uuid"));
        Gson g = new Gson();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            map.put(entry.getKey(), entry.getValue().s());
        }
        Customer c = new Customer(g.toJson(map));
        this.uuid = c.uuid;
        this.given_name = c.given_name;
        this.family_name = c.family_name;
        this.birthdate = c.birthdate;
        this.email = c.email;
        this.phone_number = c.phone_number;
        this.phone_number_verified = c.phone_number_verified;
    }

    public String toString() {
        Gson g = new Gson();
        return g.toJson(this);
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                map.put(field.getName(), field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public Map<String, AttributeValue> toDynamoMap() {
        Map<String, AttributeValue> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                String value = field.get(this).toString();
                AttributeValue attribute = AttributeValue.builder().s(value).build();
                map.put(field.getName(), attribute);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
