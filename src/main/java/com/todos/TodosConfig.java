package com.todos;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("todo's")
public interface TodosConfig extends Config
{
	@ConfigItem(
			keyName = "todoData",
			name = "",
			description = ""
	)
	default String todoData() { return ""; }

	@ConfigItem(
			keyName = "todoData",
			name = "",
			description = ""
	)
	void todoData(String str);
}
