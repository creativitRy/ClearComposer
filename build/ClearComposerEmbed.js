	var jwScript = {
		infoIcon: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA0AAAANCAYAAABy6+R8AAAACXBIWXMAAAsTAAALEwEAmpwYAAAA/ElEQVQokY2SwYmEQBBFXzWDIpiECQgjlYoxTBCzBw+zQZiNdxsFEzAJURCW2ou99MjK7r80Xbz6n+4qMTOCvPd34AEoUAED4IFWVcfAiZnRdd0tz/OnmTUigpnxy9nM8/yq6/rLAeR5/gSa4JRlGapKlmVEaoqi+ACQvu/vZjYAiAgASZJQliXTNLHvOwDhGSJS3YBHgIP2fcd7/1aLmIcDNP4MgDRNUVXSNH2rH5w6oDonOefO7vG9csBwTrrSwQ0O8GfHKx2cd0BrZvyVFjGtU9VRRH6GetUgIohIo6qjA1iW5ZNouOu64r1n27a4t5nn+QXHGgX9d/e+ARALfJbH3ASqAAAAAElFTkSuQmCC",
		appName: "ClearComposer",		
		infoSrc: ""
	};
	var showArm = false;
		
	function jwTranslate(key) 
	{
		if (typeof translations === 'undefined') return key;
  		var result = translations[key];
  		if (result == null) return key;
  		return result;
  	}	
	
	function jwIsMac()
	{
		return navigator.platform.indexOf("Mac") != -1;				
	}
	
	function jwIsWin()
	{
		return navigator.platform.indexOf("Win") != -1;
	}
	
	function jwIsLin()
	{
		return navigator.platform.indexOf("Linux") != -1 || navigator.platform.indexOf("X11") != -1;
	}
	
	function jwIncScriptCount()
	{
		var count = 0;
		
		if (window.jwScriptCount)
			count = window.jwScriptCount;
		
		count++;
		
		window.jwScriptCount = count;
		return count-1;
	}
	
	function jwGetHead()
	{
		var elements = document.getElementsByTagName("head");
		if (elements.length > 0)
			return elements[0];
				
		var headElement = document.createElement('head');
		document.body.appendChild(headElement);
		return headElement;
  	}
	
	if (!window.getComputedStyle) 
	{
    	window.getComputedStyle = function(el, pseudo) 
    	{
        	this.el = el;
        	this.getPropertyValue = function(prop) 
        	{
            	var re = /(\-([a-z]){1})/g;
            	if (prop == 'float') prop = 'styleFloat';
            	if (re.test(prop)) 
            	{
                	prop = prop.replace(re, function () 
                	{
                    	return arguments[2].toUpperCase();
                	});
            	}
            	return el.currentStyle[prop] ? el.currentStyle[prop] : null;
        	}
        	return this;
    	}
	}
	
	function jwSwitch(switchTo, n)
	{			
		var recommendedLink = document.getElementById("jw_recommended_"+n);
		var offlineLink = document.getElementById("jw_allOffline_"+n);
		var onlineLink = document.getElementById("jw_allOnline_"+n);	
		var infoLink = document.getElementById("jw_infoDiv_"+n);		
		var appletDiv = document.getElementById("jw_appletDiv_"+n);
		
		if (switchTo == "recommended")
		{
			if (appletDiv != null) appletDiv.className = "jw_topPaddedDiv";
			
			jwShow("jw_recommendedDiv1", n);
			jwShow("jw_recommendedDiv2", n);
			jwShow("jw_appletDiv", n);
			jwHide("jw_allOfflineDiv", n);
			jwHide("jw_allOnlineDiv", n);
			jwHide("jw_infoDiv", n);			
			
			recommendedLink.className = "jw_recommendedLink";
			offlineLink.className = "jw_Link";
			onlineLink.className = "jw_Link";										
		}
		else if (switchTo == "online")
		{
			if (appletDiv != null) appletDiv.className = "";
		
			jwHide("jw_recommendedDiv1", n);
			jwHide("jw_recommendedDiv2", n);
			jwShow("jw_appletDiv", n);			
			jwHide("jw_allOfflineDiv", n);
			jwShow("jw_allOnlineDiv", n);
			jwHide("jw_infoDiv", n);
			
			recommendedLink.className = "jw_Link";
			offlineLink.className = "jw_Link";
			onlineLink.className = "jw_recommendedLink";
		}
		else if (switchTo == "offline")
		{
			if (appletDiv != null) appletDiv.className = "";
			
			jwHide("jw_recommendedDiv1", n);
			jwHide("jw_recommendedDiv2", n);
			jwShow("jw_appletDiv", n);			
			jwShow("jw_allOfflineDiv", n);
			jwHide("jw_allOnlineDiv", n);
			jwHide("jw_infoDiv", n);
						
			recommendedLink.className = "jw_Link";
			offlineLink.className = "jw_recommendedLink";
			onlineLink.className = "jw_Link";					
		}	
		else if (switchTo == "info")
		{
			if (appletDiv != null) appletDiv.className = "";
			jwHide("jw_recommendedDiv1", n);
			jwHide("jw_recommendedDiv2", n);
			jwHide("jw_allOfflineDiv", n);
			jwHide("jw_allOnlineDiv", n);
			jwHide("jw_appletDiv", n);
			jwShow("jw_infoDiv", n);
						
			recommendedLink.className = "jw_Link";
			offlineLink.className = "jw_Link";
			onlineLink.className = "jw_Link";					
		}
	}
	
	function jwConstructFilename(n, isOnline, os, postfix, is64Bit, isArm)
	{
		if (postfix == null)
			postfix = "";
			
		if (os == "mac")
			return jwScripts[n].appName+"-macos"+(is64Bit ? "64" : "32")+"-"+(isOnline ? "online" : "offline")+".dmg"+postfix;
		if (os == "windows")
			return jwScripts[n].appName+"-windows"+(is64Bit ? "64" : "32")+"-"+(isOnline ? "online" : "offline")+".exe"+postfix;
		if (os == "linux" || os == "linuxarm")
			return jwScripts[n].appName+"-linux"+(is64Bit ? "64" : "32")+(isArm ? "arm" : "")+"-"+(isOnline ? "online" : "offline")+".tar"+postfix;
	}
	
	function jwGetDetectedFilename(n, isOnline, postfix)
	{			
		// <App Name>-<OS><Arch>-<online/offline> (.exe/.dmg)
		if (jwIsMac())
			return jwConstructFilename(n, isOnline, "mac", postfix, jwDetect64Bit(), jwDetectARM());
		if (jwIsWin())
			return jwConstructFilename(n, isOnline, "windows", postfix, jwDetect64Bit(), jwDetectARM());
		if (jwIsLin())
			return jwConstructFilename(n, isOnline, "linux", postfix, jwDetect64Bit(), jwDetectARM());
		return null;
	}		
	
	function jwOpenLink(href, isRecommended)			
	{
		document.write("<a class='jw_cleanlink' href='"+href+"'>");
	}
	
	function jwCloseLink()
	{
		document.write("</a>");
	}
				
	function jwOpenButton(isRecommended, align, tag)
	{
		if (align == null)
			align = "left";
		document.write("<div align='"+align+"' class='jw_shadowedFont jw_roundBorder ");
		if (isRecommended)
			document.write("jw_buttonSelected jw_blueGradient");
		else
			document.write("jw_button jw_grayGradient");
		document.write("'");
		if (tag != null)
			document.write("id='"+tag+"'");
		document.write(">");	
	}
		
	function jwOpenAppletButton(n, isRecommended, postFix)
	{		
		var	align = "left";
		document.write("<div id='jwAppletDiv_"+n+"' align='"+align+"' class='jw_shadowedFont jw_roundBorder ");
		if (isRecommended)
			document.write("jw_buttonSelected jw_blueGradient");
		else
			document.write("jw_button jw_grayGradient");			
			
		var escapedPostFix = postFix.replace(/'/g,"\\'");					
			
		document.write("' onclick=\"jwLoadApplet(");
		document.write(n);
		document.write(",");		
		document.write("'"+isRecommended+"'");
		document.write(",");
		document.write("'"+escapedPostFix+"','false');\"");		
		document.write(">");	
	}
	
	function jwCloseButton()
	{
		document.write("</div>");
	}
	
	function jwEmbedLogo(n)
	{
		if (jwScripts[n].imageURL == null) return;
		document.write("<div align='center'>");
		document.write("   <img class='jw_resizeImage' src='");		
		document.write(jwScripts[n].imageURL);
		document.write("'/>"); 
		document.write("</div>");
	}
	
	function jwAddAllOptions(n, os, type, urlPostfix)
	{
		var isArm = (os == "linuxarm");
		var isRecommended = false;
		var isOnline = type=="online";
			document.write("<tr class='jw_noborder'>");
				document.write("<td class='jw_optionLabelCell' nowrap>");
					if (os == "windows") document.write("Windows");
					if (os == "mac") document.write("Mac OS");
					if (os == "linux") document.write("Linux x86");
					if (os == "linuxarm") document.write("Linux ARM");
					if (type == "offline") document.write(" "+jwTranslate("Offline"));
					if (type == "online") document.write(" "+jwTranslate("Online"));							
				document.write("</td>");						
				document.write("<td class='jw_optionContentCell jw_nowrapCell'>");
					jwOpenLink(jwScripts[n].updateURL + jwConstructFilename(n, isOnline, os, urlPostfix, false, isArm), isRecommended);
						jwOpenButton(isRecommended, "center", null);	
							document.write("32bit");
						jwCloseButton();
					jwCloseLink();
				document.write("</td>");
				if (os != "linuxarm")
				{
					document.write("<td class='jw_optionContentCell jw_nowrapCell'>");
						jwOpenLink(jwScripts[n].updateURL + jwConstructFilename(n, isOnline, os, urlPostfix, true, isArm), isRecommended);
							jwOpenButton(isRecommended, "center", null);
								document.write("64bit");
							jwCloseButton();
						jwCloseLink();
					document.write("</td>");
				}
			document.write("</tr>");								
	}
	
	function jwAddSpecificDownloads(n, type, urlPostfix)
	{
		document.write("<table class='jw_cleanFont jw_collapsedTable'>");
			jwAddAllOptions(n, "windows", type, urlPostfix);
			jwAddAllOptions(n, "linux", type, urlPostfix);
			if (showArm) jwAddAllOptions(n, "linuxarm", type, urlPostfix);
			jwAddAllOptions(n, "mac", type, urlPostfix);					
		document.write("</table>");
	}
	
	function jwGetHeight()
	{
		var standaloneDiv = document.getElementById('jwOfflineButton');
		if (standaloneDiv != null && standaloneDiv.clientHeight > 0)
			return standaloneDiv.clientHeight - 4;
		standaloneDiv = document.getElementById('jwOnlineButton');
		if (standaloneDiv != null && standaloneDiv.clientHeight > 0)
			return standaloneDiv.clientHeight - 4;
		return 21;
	}
	
	function jwGetWidth()
	{
		var standaloneDiv = document.getElementById('jwOfflineButton');
		if (standaloneDiv != null && standaloneDiv.clientWidth > 0)
			return standaloneDiv.clientWidth - 4;
		standaloneDiv = document.getElementById('jwOnlineButton');
		if (standaloneDiv != null && standaloneDiv.clientWidth > 0)
			return standaloneDiv.clientWidth - 4;
		return 254;
	}
	
	function jwLoadApplet(n, isRecommended, postFix, autoRun)
	{
		if (jwScripts[n].appletIsLoaded)
			return;
		jwScripts[n].appletIsLoaded = true;
		
		var height = jwGetHeight();		
		var width = jwGetWidth();
		
		var appletDivElement = document.getElementById('jwAppletDiv_'+n);
		
		// Disable the onclick behaviour
		appletDivElement.onclick = '';
		appletDivElement.style.padding = '2px';
		
		var appletContent = jwEmbedApplet(n, width, height, postFix, autoRun);
		appletDivElement.innerHTML = appletContent;
	}
	
	function jwEmbedApplet(n, width, height, postFix, autoRun)
	{		
		var updateURL = jwScripts[n].updateURL;
		if (updateURL.charAt(updateURL.length-1) == '/')
			updateURL = updateURL.substring(0, updateURL.length-1);
	
		var appletDivElement = document.getElementById('jwAppletDiv_'+n);
		
		var style = window.getComputedStyle(appletDivElement, null);		
		var bg = style.background;
		var topColor = "";
		var bottomColor = "";
		if (bg != null)
		{
			var gradientIndex = bg.indexOf('gradient');
			
			var open1 = bg.indexOf('rgb', gradientIndex);
			var close1 = bg.indexOf(')', open1);
	
			var open2 = bg.indexOf('rgb', close1);
			var close2 = bg.indexOf(')', open2);
	
			topColor = bg.substring(open1, close1+1);
			bottomColor = bg.substring(open2, close2+1);
		}		
		
		var additionalParameters = "";
		var language = "en";
		var parameterList = "";
		if (postFix != null && postFix.length > 0)
		{		
			var splitPostFix = postFix.split('&');
			for (var i=0; i<splitPostFix.length; i++)
			{	
				var equalsIndex = splitPostFix[i].indexOf('=');					
				if (equalsIndex != -1)
				{
					var name = splitPostFix[i].substring(0,equalsIndex);
					var value = splitPostFix[i].substring(equalsIndex+1);
					
					if (name.indexOf('?') == 0)	name = name.substring(1);
					additionalParameters += "<param name='"+name+"' value='"+value+"' />";
					if (parameterList.length > 0)
						parameterList += ",";
					parameterList += name;
					
					if (name == "language")
						language = value;
				}
				else 
				{
					var name = splitPostFix[i]
					if (name.indexOf('?') == 0)	name = name.substring(1);
					additionalParameters += "<param name='"+name+"' value='' />";
					if (parameterList.length > 0)
						parameterList += ",";
					parameterList += name;
				}
			}						
		}
		
		if (!(typeof addAppParams === 'undefined'))
		{
			for (key in addAppParams)
			{
				additionalParameters += "<param name='"+key+"' value='"+addAppParams[key]+"' />";
				if (parameterList.length > 0)
					parameterList += ",";
				parameterList += key;
			}
		}
		
		if (parameterList.length > 0)
			additionalParameters += "<param name='jwParameterList' value='"+parameterList+"' />";		
		
		var appletContent = "";		
		
		appletContent += "<!--[if !IE]> -->";
      	appletContent += "<object classid='java:jwrapper.appletwrapper.JWAppletWrapper.class' "; 
		appletContent += "        type='application/x-java-applet' ";
		appletContent += "        archive='ClearComposerApplet.jar' ";
		appletContent += "        height='"+height+"' width='"+width+"' >";
		
		// Add in the archive here.
		appletContent += "  			<param name='archive' value='";
		var jars = 'ClearComposerApplet.jar'.split(",");
		for (var i=0; i<jars.length; i++)
		{
			if (i > 0)
				appletContent += ",";
			appletContent += updateURL+"/"+jars[i];
		}
		appletContent += "' />";
		
		appletContent += "  	<param name='persistState' value='false' />";
		appletContent += "  	<param name='update_url' value='"+updateURL+"' />";
		appletContent += "  	<param name='app_name' value='"+jwScripts[n].appName+"' />";
		if (jwScripts[n].imageURL != null)
			appletContent += "  	<param name='splash_image' value='"+jwScripts[n].imageURL+"' />";
		if (autoRun != null)
			appletContent += "  	<param name='auto_run' value='"+autoRun+"' />";
			
		appletContent += "  	<param name='supported_langs' value='"+language+"' />";			
		appletContent += "  	<param name='gradientTop' value='"+topColor+"' />";
		appletContent += "  	<param name='gradientBottom' value='"+bottomColor+"' />";
		appletContent += "  	<param name='name' value='"+jwScripts[n].appName+"' />";
		
		if (autoRun != null && autoRun == "true")
			appletContent += "  	<param name='txt_launchnow' value='"+jwTranslate("Loading")+" "+jwScripts[n].appName+"...' />";
		else
			appletContent += "  	<param name='txt_launchnow' value='"+jwTranslate("Start")+" "+jwScripts[n].appName+"' />";
		
		appletContent += additionalParameters;
		
      	appletContent += "<!--<![endif]-->";
      	
        appletContent += "		<object classid='clsid:8AD9C840-044E-11D1-B3E9-00805F499D93' "; 
        appletContent += "				codebase='http://javadl.sun.com/webapps/download/GetFile/1.7.0_21-b11/windows-i586/xpiinstall.exe#Version=1,4,0,0' ";
        appletContent += "        		height='"+height+"' width='"+width+"' >";        
        // Note that we specify the class here only for IE.
        appletContent += "  			<param name='code' value='jwrapper.appletwrapper.JWAppletWrapper.class' />";		
        
        // Add in the archive here.
        // Add in the archive here.
		appletContent += "  			<param name='archive' value='";
		var jars = 'ClearComposerApplet.jar'.split(",");
		for (var i=0; i<jars.length; i++)
		{
			if (i > 0)
				appletContent += ",";
			appletContent += updateURL+"/"+jars[i];
		}
		appletContent += "' />";
		
		appletContent += "  			<param name='persistState' value='false' />";
		appletContent += "  			<param name='update_url' value='"+updateURL+"' />";
		appletContent += "			  	<param name='app_name' value='"+jwScripts[n].appName+"' />";
		if (jwScripts[n].imageURL != null)
			appletContent += "  		<param name='splash_image' value='"+jwScripts[n].imageURL+"' />";
		if (autoRun != null)
			appletContent += "  		<param name='auto_run' value='"+autoRun+"' />";
			
		appletContent += "  			<param name='supported_langs' value='"+language+"' />";			
		appletContent += "  			<param name='gradientTop' value='"+topColor+"' />";
		appletContent += "  			<param name='gradientBottom' value='"+bottomColor+"' />";
		appletContent += "              <param name='name' value='"+jwScripts[n].appName+"' />";
        
		if (autoRun != null && autoRun == "true")
			appletContent += "  			<param name='txt_launchnow' value='"+jwTranslate("Loading")+" "+jwScripts[n].appName+"...' />";
		else
			appletContent += "  			<param name='txt_launchnow' value='"+jwTranslate("Start")+" "+jwScripts[n].appName+"' />";

		
		appletContent += additionalParameters;
			        
        appletContent += "		</object> ";
        appletContent += "<!--[if !IE]>--> ";
      	appletContent += "</object> ";
      	appletContent += "<!--<![endif]--> ";    	
				
		return appletContent;		
	}
	
	function jwStringHostnameFrom(url)
	{
		var doubleSlash = url.indexOf("://");
		if (doubleSlash != -1)
		{
			var nextSlash = url.indexOf("/", doubleSlash+3);
			return url.substring(0, nextSlash);
		}
		else
		{
			var nextSlash = url.indexOf("/");
			return url.substring(0, nextSlash);
		}
	}
	
	function jwAddOfflineButton(n, id, postfix, isRecommended)
	{
		document.write("<div id='"+id+"' class='jw_topPaddedDiv'>");
			jwOpenLink(jwScripts[n].updateURL+jwGetDetectedFilename(n, false, postfix), isRecommended);
				jwOpenButton(isRecommended, null, "jwOfflineButton");	
					if (typeof jwScripts[n].customOffline == 'undefined')
						document.write(jwTranslate("Download Full Installer"));
					else
						document.write(jwTranslate(jwScripts[n].customOffline));
				jwCloseButton();
			jwCloseLink();
		document.write("</div>");
	}
	
	function jwAddOnlineButton(n, id, postfix, isRecommended)
	{		
		document.write("<div id='"+id+"' class='jw_topPaddedDiv'>");
			jwOpenLink(jwScripts[n].updateURL+jwGetDetectedFilename(n, true, postfix), isRecommended);
				jwOpenButton(isRecommended, null, "jwOnlineButton");
					if (typeof jwScripts[n].customOnline == 'undefined')
						document.write(jwTranslate("Download")+" "+jwScripts[n].appName);
					else
						document.write(jwTranslate(jwScripts[n].customOnline));
				jwCloseButton();
			jwCloseLink();
		document.write("</div>");				
	}
	
	function jwAddAppletButton(n, id, postfix, isRecommended)
	{
		document.write("<div id='"+id+"' class='jw_topPaddedDiv'>");
			jwOpenAppletButton(n, isRecommended, postfix);						
				if (typeof jwScripts[n].customJava == 'undefined')
					document.write(jwTranslate("Launch using Java"));								
				else
					document.write(jwTranslate(jwScripts[n].customJava));			
			jwCloseButton();
		document.write("</div>");
	}
	
	function jwHide(divID, scriptID)
	{
		var element = document.getElementById(divID+"_"+scriptID);
		if (element != null)
			element.style.display='none';
	}
	
	function jwShow(divID, scriptID)
	{
		var element = document.getElementById(divID+"_"+scriptID);
		if (element != null)
			element.style.display='block';
	}
	
	function jwAddInfoDiv(n)
	{
		var jwInfoSrc = jwScripts[n].infoSrc;
		if (typeof jwScripts === 'undefined' || jwInfoSrc.length == 0)
			jwInfoSrc = "Powered by<br><br>JWrapper <a href=\"http://jwrapper.com\">Java Installer</a>";
		document.write(jwInfoSrc);
	}
	
	function processPostFix(n, postFix)
	{
		var hostname = jwStringHostnameFrom(jwScripts[n].updateURL);
		if (hostname.length > 0)
		{
			if (postFix.length == 0)
				postFix = "?hostname="+encodeURIComponent(hostname);
			else
			{
				postFix = postFix + "&hostname="+encodeURIComponent(hostname);
				if (postFix.charAt(0) != '?') postFix = '?'+postFix;
			}
		}
		return postFix+"&ie=ie.exe";
	}
		
	function jwEmbedDeploymentOptions(n)
	{
		var orders = jwScripts[n].configuration.split(",");
		var runApplet = false;		
		var postFix = processPostFix(n, jwScripts[n].postFix);
					
		// This is the main div that is shown the first time
		document.write("<div style='padding-top: 1px' id='jw_onlyRecommededDiv_"+n+"' class='jw_cleanFont'>");
		
			for (var i=0; i<orders.length; i++)
			{
				var current = orders[i];
				var isRecommended = current.indexOf("*") != -1;
				if (!isRecommended)
					continue;							
				if (current.indexOf("offline") != -1)
					jwAddOfflineButton(n, "", postFix, isRecommended);
				else if (current.indexOf("online") != -1)
					jwAddOnlineButton(n, "", postFix, isRecommended);
				else if (current.indexOf("applet") != -1)
				{				
					jwAddAppletButton(n, "", postFix, isRecommended);
					if (current.indexOf("applet_run") != -1)
						runApplet = true;	
				}
				else
					document.write("Warning: unknown configuration entry '"+current+"'");
			}
			
			if (jwScripts[n].showAllDownloads)
			{
				document.write("<div class='jw_cleanFont' style='padding-top: 10px'>");
					document.write("<span id='jw_allDownloads_"+n+"' onclick='jwHide(\"jw_onlyRecommededDiv\","+n+"); jwShow(\"jw_mainDiv\", "+n+"); ' class='jw_Link'>");
					document.write(jwTranslate("All Downloads"));
					document.write("</span>");
				document.write("</div>");
			}
		document.write("</div>");
				
		// This is the set of 3 buttons with links shown as alternatives
		document.write("<div style='display:none;' id='jw_mainDiv_"+n+"'>");
			document.write("<div style='display:none;' id='jw_allOnlineDiv_"+n+"'>");				
				jwAddSpecificDownloads(n, "online", postFix);
			document.write("</div>");
			document.write("<div style='display:none;' id='jw_allOfflineDiv_"+n+"'>");				
				jwAddSpecificDownloads(n, "offline", postFix);
			document.write("</div>");
			document.write("<div style='display:none;' id='jw_infoDiv_"+n+"' class='jw_infoDiv jw_mediumFont jw_roundBorder jw_cleanFont'>");	
				jwAddInfoDiv(n);
			document.write("</div>");
							
			document.write("<div style='padding-top: 1px' class='jw_cleanFont'>");				
				for (var i=0; i<orders.length; i++)
				{
					var current = orders[i];
					var isRecommended = current.indexOf("*") != -1;
					if (current.indexOf("offline") != -1)
						jwAddOfflineButton(n, "jw_recommendedDiv1_"+n, postFix, isRecommended);
					else if (current.indexOf("online") != -1)
						jwAddOnlineButton(n, "jw_recommendedDiv2_"+n, postFix, isRecommended);
					else if (current.indexOf("applet") != -1)
						jwAddAppletButton(n, "jw_appletDiv_"+n, postFix, isRecommended);				
					else
						document.write("Warning: unknown configuration entry '"+current+"'");
				}
			document.write("</div>");
			
			// Include the links table in this div since we have the 'Alternative Downloads' link above
			jwEmbedSwitcherLinks(n);
						
		document.write("</div>");
		
		if (runApplet)
			jwLoadApplet(n, isRecommended, postFix, true)		
	}
	
	function jwEmbedWithSettings(n)
	{
		// This adds a style node which hides the main div
		// until the lazily loaded styles are fetched, after which the div is shown.
		// This prevents the unstyled content from being shown.
		var css = document.createElement('style');
		css.type = 'text/css';
		var styles = '.jw_topLevelMainDiv {display:none;}';
		if (css.styleSheet) css.styleSheet.cssText = styles;
		else css.appendChild(document.createTextNode(styles));

		jwGetHead().appendChild(css);
		
		document.write("<div id='jw_topLevelMainDiv_"+n+"' class='jw_topLevelMainDiv jw_cleanFont jw_mainDiv'>");
			jwEmbedLogo(n);
			if (jwScripts[n].showAppName)
			{
				document.write("<div class='jw_appName'>");
				document.write(jwScripts[n].appName);
				document.write("</div>");
			}
				
			jwEmbedDeploymentOptions(n);						
		document.write("</div>");
	}
	
	function jwEmbedSwitcherLinks(n)
	{
		document.write("<div align='center' style='padding-top:10px'>");
			document.write("<table class='jw_collapsedTable jw_smallerLink'>");
				document.write("<tr class='jw_noborder'>");
					if (jwScripts[n].showInfo)
					{
						document.write("<td class='jw_nowrapCell jw_leftEdgeCell'>");
						document.write("</td>");
					}
					document.write("<td class='jw_nowrapCell'>");
						document.write("<span id='jw_recommended_"+n+"' onclick='jwSwitch(\"recommended\", "+n+");' class='jw_recommendedLink'>");
						document.write(jwTranslate("Recommended"));
						document.write("</span>");
					document.write("</td>");
					document.write("<td class='jw_nowrapCell' >");
						document.write("<span class='jw_lightFont''>|</span>");
					document.write("</td>");
					document.write("<td class='jw_nowrapCell'>");
						document.write("<span id='jw_allOnline_"+n+"'' onclick='jwSwitch(\"online\", "+n+");' class='jw_Link'>");
						document.write(jwTranslate("All Online"));
						document.write("</span>");
					document.write("</td>");
					document.write("<td class='jw_nowrapCell'>");
						document.write("<span class='jw_lightFont'>|</span>");
					document.write("</td>");
					document.write("<td class='jw_nowrapCell'>");
						document.write("<span id='jw_allOffline_"+n+"'' onclick='jwSwitch(\"offline\", "+n+");' class='jw_Link'>");
						document.write(jwTranslate("All Offline"));
						document.write("</span>");
					document.write("</td>");
					if (jwScripts[n].showInfo)
					{
						document.write("<td class='jw_nowrapCell jw_rightEdgeCell'>");
							document.write("<img id='jw_infoButton_"+n+"'' class='jw_Link' src='"+jwScripts[n].infoIcon+"' onclick='jwSwitch(\"info\", "+n+");'/>");
						document.write("</td>");
					}
				document.write("</tr>");
			document.write("</table>");
		document.write("</div>");
	}
	
	function jwGetJavascriptUpdateURL(scriptElement)
	{
		var jwUpdateURL = scriptElement.getAttribute('jwUpdateURL');
		if (jwUpdateURL != null)
			return jwUpdateURL;
		var url = scriptElement.getAttribute('src');
		if (url.charAt(0) == '/')
		{
			var browserURL = document.location.href;
			if (browserURL.indexOf("://") == -1)
			{
				var protocol = document.location.protocol;
				if (protocol == null || protocol.length == 0)
					protocol = "http";
				browserURL = protocol+"://"+browserURL;
			}
						
			url = jwStringHostnameFrom(browserURL) + url;
		} 
			
		var parameterIndex = url.lastIndexOf('?');
		if (parameterIndex == -1)
			parameterIndex = url.length-1;
			
		var lastSlash = url.lastIndexOf('/', parameterIndex);
		if (lastSlash != -1)
			url = url.substring(0, lastSlash+1);
						
		return url;
	}
	
	function jwGetImageURL(scriptElement, jwUpdateURL, jwAppName)
	{
		var imageURL = scriptElement.getAttribute('imageURL');
		if (imageURL != null)
		{
			if (imageURL.charAt(0) == '/')
			{
				// Relative URL
				var jwUpdateURLHostname = jwStringHostnameFrom(jwUpdateURL);
				return jwUpdateURLHostname+imageURL;
			}
			else
			{
				// Absolte URL is OK - don't add on a host				
			}
		}
		else
		{
			// If there is no custom URL use the JWrapper default
			imageURL = jwUpdateURL+"/JWrapper-"+jwAppName+"-splash.png";
		}
		
		if (jwToBoolean(scriptElement.getAttribute('showImage'), true))
			return imageURL;
		return null;
	}
	
	function jwToBoolean(value, defaultValue)
	{
		if (value != null)
		{
			if (value == 'no' || value == 'false')
				return false;
			if (value == 'yes' || value == 'true')
				return true;
		}
		return defaultValue;
	}
				
	function jwGetCSSLink(scriptElement)
	{
		var url = scriptElement.getAttribute('src');
		if (url.lastIndexOf('?') != -1) url = url.substring(0, url.lastIndexOf('?'));
		var jsIndex = url.lastIndexOf('.js');
		url = url.substring(0, jsIndex) + ".css";
		return url;
	}
		
	function jwGetJavascriptConfiguration(scriptElement)
	{
		var config = scriptElement.getAttribute('configuration');
		if (config == null)
			return "online*,offline,applet";
		return config;		
	}
	
	function jwLoadCSSDynamically(cssLink)
	{
		var fileref = document.createElement("link");
  		fileref.setAttribute("rel", "stylesheet");
  		fileref.setAttribute("type", "text/css");
  		fileref.setAttribute("href", cssLink);
  		jwGetHead().appendChild(fileref);  		
 	}

 	function jwDetectARM()
 	{
		return navigator.platform.indexOf(" arm") != -1;
 	}

 	function jwDetect64Bit()
 	{
 		var agent = navigator.userAgent;
 	
 		if (jwIsMac())
   		{   			
   			var regex = /Mac OS X (\d+)[\.\_](\d+)[\.\_]?(\d*)/g;
   			var myArray = regex.exec(agent);
   			if (myArray.length == 3)
   			{
   				if (myArray[1] > 10) return true;
   				if (myArray[1] == 10 && myArray[2] > 7) return true;
   			}
   			else if (myArray.length == 4)
   			{
   			   	if (myArray[1] > 10) return true;
   				if (myArray[1] == 10 && myArray[2] > 7) return true;
   				if (myArray[1] == 10 && myArray[2] > 7 && myArray[3] >= 3) return true;
   			}
   			return false;
   		}
 	
 		if (agent.indexOf("WOW64") != -1 || 
    		agent.indexOf("Win64") != -1 ||
    		agent.indexOf("x86_64") != -1)
    	{
   			return true;
   		}
   		   		
   		if (navigator.cpuClass != null && navigator.cpuClass.indexOf("64") != -1)
   			return true;
   			
   		return false;
	} 
	function jwGetExistingPostFix(scriptElement)
	{
		var url = scriptElement.getAttribute('src');
		var jsIndex = url.lastIndexOf('.js');
		var postFix = url.substring(jsIndex+3);
		if (postFix.length > 0 && postFix.charAt(0) == '?')
			postFix = postFix.substring(1);
						
		var existingParameters = document.location.search;
		if (existingParameters.length == 0)
			return postFix;
		else
		{
			if (existingParameters.charAt(0) == '?')
				existingParameters = existingParameters.substring(1);
				
			var encodedParams = "";
			var split = existingParameters.split('&');
			for (var i=0; i<split.length; i++)
			{	
				if (split[i]) 
				{
				    var firstIndex = split[i].indexOf('=');
				    if (firstIndex == -1)
				    {
				        encodedParams += encodeURIComponent(decodeURIComponent(split[i]));
				    }
				    else
				    {
				        var key = split[i].substring(0, firstIndex);
				        var value = split[i].substring(firstIndex+1);
				        encodedParams += encodeURIComponent(decodeURIComponent(key))+"="+encodeURIComponent(decodeURIComponent(value));
				    }
													
					if (i < split.length-1) 
					{
						encodedParams += "&";
					}
				}
			}
				
			if (postFix.length > 0)
				return postFix+"&"+encodedParams;
			else
				return encodedParams;
		}
	}
	
	function jwGetOSDependentFilename(n, isOnline)
	{
		var postFix = processPostFix(n, jwScripts[n].postFix);
		return jwScripts[n].updateURL+jwGetDetectedFilename(n, isOnline, postFix);
	}
	
	function jwGetFirstOSDependentFilename(isOnline)
	{
		return jwGetOSDependentFilename(0, isOnline);
	}
	
	function jwGetCustomButtonText(scriptElement, n) {
		var tmp = scriptElement.getAttribute('customOnline');
		if (tmp != null) jwScripts[n].customOnline = tmp;
		tmp = scriptElement.getAttribute('customOffline');
		if (tmp != null) jwScripts[n].customOffline = tmp;
		tmp = scriptElement.getAttribute('customJava');
		if (tmp != null) jwScripts[n].customJava = tmp;
	}
	
	function jwEmbed()
	{	
		var scriptElement = document.getElementById('jwEmbed');
		var scriptElementLink = document.getElementById('jwLink');
		if (scriptElement || scriptElementLink)
		{
			var n = jwIncScriptCount();
			
			if (typeof jwScripts === 'undefined') jwScripts = [];
			jwScripts[n] = jwScript;		
			
			if (scriptElement)
			{	
				jwScript.updateURL = jwGetJavascriptUpdateURL(scriptElement);
				jwScript.configuration = jwGetJavascriptConfiguration(scriptElement);
				if (jwScript.configuration == null)	return false;
				
				jwScript.imageURL = jwGetImageURL(scriptElement, jwScript.updateURL, jwScript.appName);
				jwScript.showAppName = jwToBoolean(scriptElement.getAttribute('showAppName'), true);
				jwScript.showInfo = jwToBoolean(scriptElement.getAttribute('showInfo'), true);
				jwScript.showAllDownloads = jwToBoolean(scriptElement.getAttribute('showAllDownloads'), true);
				
				jwScript.postFix = jwGetExistingPostFix(scriptElement);	
				
				jwGetCustomButtonText(scriptElement, n);
				
				if (typeof jwScript.infoSrc === 'undefined' || jwScript.infoSrc.length == 0) jwScript.showInfo = false;
				
				jwEmbedWithSettings(n);
				
				var cssLink = jwGetCSSLink(scriptElement);
				jwLoadCSSDynamically(cssLink);
				
				scriptElement.parentNode.removeChild(scriptElement);
			}
			else if (scriptElementLink)
			{
				jwScript.updateURL = jwGetJavascriptUpdateURL(scriptElementLink);
				jwScript.postFix = jwGetExistingPostFix(scriptElementLink);	

				scriptElementLink.parentNode.removeChild(scriptElementLink);
			}
		}	
	}
	
	jwEmbed();
	
	
	