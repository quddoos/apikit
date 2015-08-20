/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.api.MuleEvent;
import org.mule.api.transport.PropertyScope;
import org.mule.module.apikit.AbstractRouter;
import org.mule.module.apikit.odata.ODataPayload;
import org.mule.module.apikit.odata.model.Entity;
import org.mule.module.apikit.odata.processor.formatters.ODataApiKitFormatter;
import org.raml.model.Raml;

public class ODataApikitProcessor extends ODataRequestProcessor
{
	private String path;
	private String query;
	private String url;
	private String entityName;

	public ODataApikitProcessor(Raml raml, String path, String query, String entityName, String url)
	{
		super(raml);
		this.path = path;
		this.query = query;
		this.entityName= entityName;
		this.url= url;
	}

	@Override
	public ODataPayload process(MuleEvent event, AbstractRouter router) throws Exception
	{
		List<Entity> entities = processEntityRequest(event, router);
		ODataPayload oDataPayload = new ODataPayload(entities);
		oDataPayload.setFormatter(new ODataApiKitFormatter(raml, entities, entityName, url));
		return oDataPayload;
	}

	public List<Entity> processEntityRequest(MuleEvent event, AbstractRouter router)
	{
		List<Entity> entities = new ArrayList<Entity>();

		try
		{
			final StringBuffer result = new StringBuffer();

			//String path = event.getMessage().getInboundProperty("http.request.path");

			String path = "/api";

			String httpRequest = path + this.path + "?" + this.query;
			String httpRequestPath = path + this.path;
			String httpQueryString = this.query;
			Map<String, String> httpQueryParams = queryToMap(query);

			event.getMessage().setProperty("http.request", httpRequest, PropertyScope.INBOUND);
			event.getMessage().setProperty("http.request.path", httpRequestPath, PropertyScope.INBOUND);
			event.getMessage().setProperty("http.query.string", httpQueryString, PropertyScope.INBOUND);
			event.getMessage().setProperty("http.query.params", httpQueryParams, PropertyScope.INBOUND);

			//Remove this poor's man debuger!
			System.out.println(httpRequest);
			System.out.println(httpRequestPath);
			System.out.println(httpQueryString);
			System.out.println(httpQueryParams);

			MuleEvent response = router.process(event);
			Object payload = response.getMessage().getPayload();

			if (payload instanceof String)
			{
				entities = getEntityList(payload);
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		if (query.contains("format=json"))
		{
			event.getMessage().setOutboundProperty("Content-Type", "application/json");
		} else
		{
			event.getMessage().setOutboundProperty("Content-Type", "application/xml");
		}

		return entities;
	}

	private Map<String, String> queryToMap(String query)
	{
		Map<String, String> queryMap = new HashMap<String, String>();

		if (query != null && query != "")
		{
			String[] queries = query.split("&");
			for (String q : queries)
			{
				String[] parts = q.split("=");
				queryMap.put(parts[0], parts[1]);
			}
		}

		return queryMap;
	}

	private List<Entity> getEntityList(Object payload) throws JSONException
	{

		List<Entity> entities = new ArrayList<Entity>();

		JSONObject response = new JSONObject(payload.toString());
		JSONArray objects = response.getJSONArray("entities");

		for (int i = 0; i < objects.length(); i++)
		{
			JSONObject j = objects.optJSONObject(i);
			Iterator it = j.keys();
			Entity e = new Entity();
			while (it.hasNext())
			{
				String n = it.next().toString();
				e.addProperty(n, j.get(n));
			}
			entities.add(e);
		}

		return entities;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}
}
