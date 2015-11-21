package org.ddpush.im.util.json;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
			return new Date( Resource.format.parse(json.getAsJsonPrimitive()
					.getAsString()).getTime());
		} catch (ParseException e) {
			throw new JsonParseException(e);
		}
	}

}
