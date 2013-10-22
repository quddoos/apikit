package org.mule.tooling.apikit.test;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForWidget;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.tooling.apikit.test.api.MuleGlobalElementWizardEditorBot;
import org.mule.tooling.apikit.test.api.MulePropertiesEditorBot;
import org.mule.tooling.apikit.test.api.MuleStudioBot;
import org.mule.tooling.apikit.test.api.StudioPreferencesEditor;
import org.mule.tooling.apikit.test.api.XmlComparer;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SimpleTests {

    private static SWTWorkbenchBot bot;
    private static MuleStudioBot muleStudioBot;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // increase timeout to 10 seconds
        SWTBotPreferences.TIMEOUT = 8000;
        // increase timeout to 1 second
        SWTBotPreferences.PLAYBACK_DELAY = 10;
        // Don't use SWTWorkbenchBot here which relies on Platform 3.x
        bot = new SWTWorkbenchBot();
        muleStudioBot = new MuleStudioBot(bot);
    }

    @Test
  public void changeAPIkitComponentName() throws Exception {
    	
    	String projectName = "dnd" + System.currentTimeMillis();
    	String flowName = "testflow";
    	muleStudioBot.createProject(projectName, "changeAPIkitComponentName");
    	muleStudioBot.createFlow(flowName, "Description of the flow");

        SWTBotGefEditor editor = new SWTBotGefEditor(bot.editorByTitle(flowName).getReference(), bot);
        editor.bot().textWithLabel("Filter:").setText("HTTP");
        editor.activateTool("HTTP").click(601,500);
        editor.bot().textWithLabel("Filter:").setText("APIkit Router");
        editor.activateTool("APIkit Router").click(70,80);
        editor.click("APIkit Router");
        
        MulePropertiesEditorBot propertiesEditorBot = new MulePropertiesEditorBot(bot);
        propertiesEditorBot.setTextValue("Display Name:", "newname");
        propertiesEditorBot.apply();
        
        editor.click("newname");
        
        String nameInsideTheViewer = propertiesEditorBot.getTextValue("Display Name:");
        Assert.assertThat(nameInsideTheViewer, CoreMatchers.is("newname"));
        propertiesEditorBot.apply(); 
        
      //XML comparison
        String expectedXml = "/resources/apikit-change-component-name-expected.xml";
		XmlComparer comparer = new XmlComparer(bot);
        comparer.compareToTheXMLUsingUI(flowName, expectedXml, true);
        
        muleStudioBot.saveAll();
    }

    @Test
    public void checkIfMuleStudioIsPairedWithASR(){
    	String token ="";
    	String host = "agent-registry.mulesoft.com";
    	String port = "443";
    	String path = "";
    	getPrincipalShell().activate();
    	SWTBotMenu file = bot.menu("Window").click();
    	file.menu("Preferences").click();
    	StudioPreferencesEditor preferences = new StudioPreferencesEditor(bot);
    	preferences.assertASRagentConfiguration(token, host, port, path);
    	
    	
    }
    
    private SWTBotShell getPrincipalShell(){
    	SWTBotShell shell = null;
    	for(int i = 0; i < bot.shells().length; i++){
    		if (bot.shells()[i].getText().endsWith("Mule Studio")){
    			shell = bot.shells()[i]; 
    		}
    	}
    	if (shell == null){
    		Assert.assertNotNull("Mule Studio shell could not be localized",null);
    	}
    	return shell;
    }
    
    @Test
    public void addAPIkitGlobalElementRouter() throws Exception {
    	
    	String projectName = "dnd" + System.currentTimeMillis();
    	String flowName = "testflow";
    	muleStudioBot.createProject(projectName, "changeAPIkitComponentName");
    	muleStudioBot.createFlow(flowName, "Description of the flow");

        SWTBotGefEditor editor = new SWTBotGefEditor(bot.editorByTitle(flowName).getReference(), bot);
        editor.bot().textWithLabel("Filter:").setText("HTTP");
        editor.activateTool("HTTP").click(601,500);
        editor.bot().textWithLabel("Filter:").setText("APIkit Router");
        editor.activateTool("APIkit Router").click(70,80);
        editor.click("APIkit Router");
        
        MulePropertiesEditorBot propertiesEditorBot = new MulePropertiesEditorBot(bot);
        propertiesEditorBot.setTextValue("Display Name:", "newname");
        MuleGlobalElementWizardEditorBot wizardEditor = propertiesEditorBot.clickTooltipButton("Add");
        
        wizardEditor.setTextValue("Name:","MyRouter");
        wizardEditor.setTextValue("YAML File:","yamlFile.yaml");
        wizardEditor.setTextValue("Console Path:","new console path");
        wizardEditor.clickButton("OK");
        propertiesEditorBot.activate();
        propertiesEditorBot.apply();
        
        //XML comparison
        String expectedXml = "resources/apikit-editing-global-element-expected.xml";
        XmlComparer comparer = new XmlComparer(bot);
        comparer.compareToTheXMLUsingUI(flowName, expectedXml, true);

        muleStudioBot.saveAll();
    }

    @Test
    public void createNewAPIkitProject(){
    	
    	String projectName = "dnd" + System.currentTimeMillis();
    	muleStudioBot.createProject(projectName, "newAPIkitProject");
    }
   
    @Test
    public void createNewAPIkitExample() throws Exception{
    	String projectName = "dnd" + System.currentTimeMillis();
    	
    	muleStudioBot.createAPIkitExample(projectName, "newAPIkitExample","REST API with APIkit");
    	//waitForEditor(withText("leagues"));
    	String flowName = "leagues";//bot.activeEditor().getTitle();
    	waitForWidget((withText(flowName)));

        String consoleText = "";
        bot.viewByTitle("Console").show();
        do{
        	consoleText = bot.viewByTitle("Console").bot().styledText().getText().toString();
        }
        while(!consoleText.contains("BUILD SUCCESS") && !consoleText.contains("BUILD FAILED"));

    	//XML comparison
        String expectedXml = "resources/apikit-example-expected.xml";
        XmlComparer comparer = new XmlComparer(bot);
        comparer.compareToTheXMLUsingUI(flowName, expectedXml, true);
        
        muleStudioBot.saveAll();
        //bot.waitUntil((ICondition)bot.viewByTitle("Console"));
    	//XML comparison
        /*String expectedXml = "/resources/apikit-samples-example-expected.xml";
        XmlComparer comparer = new XmlComparer(bot);
        comparer.compareToTheXMLUsingUI(flowName, expectedXml, true);
        */
    //    bot.widget(instanceOf(org.eclipse.swt.custom.StyledText.class), bot.viewByTitle("Console").getWidget());
       /* Widget consoleViewComposite = view.getWidget();

		StyledText swtStyledText = (StyledText) bot.widget(instanceOf(StyledText.class), consoleViewComposite);
		SWTBotStyledText styledText = new SWTBotStyledText(swtStyledText);
*/

    }
}
    
    /* @Test
    public void completeFieldsInServiceRegistryComponent() throws Exception {
      	final String ASR_NAME = "Anypoint Service Registry";

      	final MuleProjectBot projectBot = muleStudioBot.createProject("dnd", "changeAPIkitComponentName");
          final MuleConfigurationEditorBot muleConfigurationBot = projectBot.openMuleConfiguration("test");
         
      
          muleConfigurationBot.addFlow("FlowTest")
          .addHttpEndpoint("HTTP",MessageExchangePattern.REQUEST_RESPONSE,EndpointDirection.INBOUND).usingProperties().property("port","8081").property("host","localhost").property("org.mule.tooling.ui.modules.core.widgets.meta.ModeAttribute","http://www.mulesoft.org/schema/mule/http/endpoint").property("exchange-pattern","request-response").endProperties()
          .addPattern("http://www.mulesoft.org/schema/mule/core/dynamic-router","Anypoint Service Registry")
          .endFlow();

          muleConfigurationBot.done();
          
          MulePropertiesEditorBot editorBot = muleConfigurationBot.editElementProperties(FLOW_NAME,ASR_NAME);
          editorBot.setTextValue("Display Name:","ASR NAME");
          editorBot.selectComboBox(0,"All");
          editorBot.selectComboBox(1,"Consumer Key");
          editorBot.setTextValue("Service Id:","service id");
          editorBot.setTextValue("Consumer Key:","asdfadfadfadfadfs");
          editorBot.apply();
          
          
          bot.menu("File").menu("Save").click();
          
          
          //XML comparison
          String expectedXml = "/resources/asr-simple-test-expected.xml";
  		String actualXml = "dnd/src/main/app/dnd.xml";
  		XmlComparer comparer = new XmlComparer();
          comparer.compareToTheXML(actualXml, expectedXml, true);*/
          //XML comparison
  	/*	try {
  			String expectedXml = readResource("/resources/asr-simple-test-expected.xml");
  			File file = new File("/Users/federico/Documents/junit-workspace/dnd/src/main/app/dnd.xml");
  			FileInputStream fis = new FileInputStream(file);
  			String actualXml = IOUtils.toString(fis);	
  			assertIdenticalXML(expectedXml, actualXml,true);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
      }*/