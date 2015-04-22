package com.faris.fakeadmin.helper.custom;

import java.util.HashMap;
import java.util.Map;

public class CustomMap<K, V> extends HashMap<K, V> {

	public CustomMap() {
	}

	public CustomMap(Map<K, V> map) {
		this.putAll(map);
	}

	@Override
	public boolean containsKey(Object key) {
		return key != null && super.containsKey(key) && this.get(key) != null;
	}

	public V get(K key, V defaultValue) {
		try {
			V value = this.get(key);
			return value == null ? defaultValue : value;
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	@Override
	public V put(K key, V value) {
		return value == null ? this.remove(key) : super.put(key, value);
	}

	public V remove(K key, V defaultValue) {
		V value = this.remove(key);
		return value == null ? defaultValue : value;
	}

}
