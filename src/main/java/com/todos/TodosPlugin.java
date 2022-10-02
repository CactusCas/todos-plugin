package com.todos;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;


import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Tasks",
	description = "Enable the Tasks panel",
	tags = {"panel"},
	loadWhenOutdated = true
)


public class TodosPlugin extends Plugin
{

	private BufferedImage icon;
	private NavigationButton navButton;



	@Inject
	private TodosConfig config;

	@Inject
    private TodosPanel panel;

    @Inject
    private ClientToolbar clientToolbar;

	@Provides
	TodosConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TodosConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Todos started!");

		panel = injector.getInstance(TodosPanel.class);
		panel.init(config);
		icon = ImageUtil.loadImageResource(getClass(), "resources/todos_icon.png");
		navButton = NavigationButton.builder()
				.tooltip("Todo's")
				.icon(icon)
				.priority(7)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);


	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		log.info("Example stopped!");
	}

}
