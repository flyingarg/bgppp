package com.bgppp.protoprocessor;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class is a wrapper over the HashMap class.
 * Allows printing of router configuration as soon as there are changes in the router list.
 * put and remove overridden.
 * @author rajumoh
 *
 * @param <K>
 * @param <V>
 */
public class WrappedHash<K, V> extends HashMap<K, V>{
	public static Logger log = Logger.getLogger(WrappedHash.class.getName());
	@Override
	public V put(K key, V value) {
		log.info("Put:Key->"+key.toString()+"Value"+value.toString());
		return super.put(key, value);
	}
	@Override
	public V remove(Object key) {
		log.info("Remove:Key->"+key.toString()+"Value"+super.get(key).toString());
		return super.remove(key);
	}
}
