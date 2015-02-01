package scriptutils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scriptutils.common.Utils;


public class WSO2POMVerifier {
    
    public static final List<String> findMatch(String str,Pattern p){
    	List<String> matchingList = new ArrayList<String>();
        Matcher matcher = p.matcher(str);
        while(matcher.find()){
        	matchingList.add(matcher.group());
        }
        return matchingList;
    }
    
    static Pattern noDepndancyTagsPattern  = Pattern.compile("(?s)<dependency>.*?<version>.*?</version>.*?</dependency>");
    static Pattern noSnapShotInDependancyPattern  = Pattern.compile("(?s)<dependency>.*?<version>.*?SNAPSHOT.*?</version>.*?</dependency>");
    
    
    static Pattern noSnapShotInVersionPattern = Pattern.compile("<.*?\\.version>.*?SNAPSHOT.*?</.*?\\.version>"); 
    
    static Pattern noSnapShotInExportPackagePattern = Pattern.compile("(?s)<Export-Package>.*?SNAPSHOT.*?</Export-Package>");
    
    static Pattern noSnapShotInImportPackagePattern = Pattern.compile("(?s)<Import-Package>.*?SNAPSHOT.*?</Import-Package>");
    
    static HashSet<String> removedGroupIds = new HashSet<String>();
    
    static{
    	//removedGroupIds.add("org.wso2.carbon");
        removedGroupIds.add("org.wso2.appserver");
        removedGroupIds.add("oc_jag");
		removedGroupIds.add("org.wso2.amber");
		removedGroupIds.add("org.wso2.charon");
		removedGroupIds.add("org.wso2.identity");
		removedGroupIds.add("org.wso2.dss.connectors.mongodb");
		removedGroupIds.add("org.wso2.governance");
		removedGroupIds.add("org.wso2.ss");

		removedGroupIds.add("org.wso2.eclipse");
		//removedGroupIds.add("org.apache.axis2.transport");
		removedGroupIds.add("org.wso2.appfactory");
		removedGroupIds.add("org.wso2.carbon.automation");
		removedGroupIds.add("org.wso2.carbon.automation");
		removedGroupIds.add("org.jaggeryjs");
		removedGroupIds.add("org.jaggeryjs.modules");
		removedGroupIds.add("org.wso2.store");
		removedGroupIds.add("org.wso2.ues");
		removedGroupIds.add("org.wso2.store");

		removedGroupIds.add("org.wso2.siddhi");
		removedGroupIds.add("org.wso2.cep");
    }    
    
    static String[] notAllowedProperties = { "wso2carbon.version", "project.version" , "carbon.version" , "carbon.platform.version" }; 
        
    
    public static class POMWarnBean{
    	int errorCode;
    	String l1Group;
    	String l2Group;
    	List<String> matches; 
    	String path;
		public POMWarnBean(int errorCode, String l1Group, String l2Group,
				String path, List<String> matches) {
			super();
			this.errorCode = errorCode;
			this.l1Group = l1Group;
			this.l2Group = l2Group;
			this.path = path;
			this.matches = matches;
		}
		@Override
		public String toString() {
			return path + ":" + errorCode + " "+ l1Group + " " + l2Group + " " + matches; 
		}
    	
    	
    }
    
    static Map<String, List<POMWarnBean>> wranningsList = new HashMap<String, List<WSO2POMVerifier.POMWarnBean>>();
    static{
    	wranningsList.put("noDepndancyTagsPattern", new ArrayList<POMWarnBean>());
    	wranningsList.put("noSnapShotInDependancyPattern", new ArrayList<POMWarnBean>());
    	wranningsList.put("noSnapShotInVersionPattern", new ArrayList<POMWarnBean>());
    	wranningsList.put("noSnapShotInImportExportPackagePattern", new ArrayList<POMWarnBean>());
    	wranningsList.put("removedGroupIDFound", new ArrayList<POMWarnBean>());
    	wranningsList.put("NotAllowedPropertyFound", new ArrayList<POMWarnBean>());
    }
    
    
    
    public static void checkPOM(String path, String contentAsStr){
    	// 
    	List<String> matches; 
//    	matches = findMatch(contentAsStr, noDepndancyTagsPattern);
//    	if(matches.size() > 0){
//    		wranningsList.get("noDepndancyTagsPattern").add(new POMWarnBean(2, "noDepndancyTagsPattern", null, path, matches));
//    	}

    	matches = findMatch(contentAsStr, noSnapShotInDependancyPattern);
    	if(matches.size() > 0){
    		wranningsList.get("noSnapShotInDependancyPattern").add(new POMWarnBean(2, "noSnapShotInDependancyPattern", null, path, matches));
    	}

    	matches = findMatch(contentAsStr, noSnapShotInVersionPattern);
    	if(matches.size() > 0){
    		wranningsList.get("noSnapShotInVersionPattern").add(new POMWarnBean(2, "noSnapShotInVersionPattern", null, path, matches));
    	}
    	
    	matches = findMatch(contentAsStr, noSnapShotInExportPackagePattern);
    	if(matches.size() > 0){
    		wranningsList.get("noSnapShotInImportExportPackagePattern").add(new POMWarnBean(2, "noSnapShotInImportExportPackagePattern", null, path, matches));
    	}
    	
    	matches = findMatch(contentAsStr, noSnapShotInImportPackagePattern);
    	if(matches.size() > 0){
    		wranningsList.get("noSnapShotInImportExportPackagePattern").add(new POMWarnBean(2, "noSnapShotInImportExportPackagePattern", null, path, matches));
    	}    	
    	
    	for(String gid: removedGroupIds){
        	if(contentAsStr.contains(gid)){
        		wranningsList.get("removedGroupIDFound").add(new POMWarnBean(2, "removedGroupIDFound", gid, path, null));
        	}
    	}
    	
    	
    	for(String property: notAllowedProperties){
        	if(contentAsStr.contains(property)){
        		wranningsList.get("NotAllowedPropertyFound").add(new POMWarnBean(2, "NotAllowedPropertyFound", property, path, null));
        	}
    	}

    }

    public static void findAllPOMs(File dir, List<File> pomFiles){
    	File[] fileList = dir.listFiles();
    	for(File file: fileList){
    		if(file.isFile() && file.getName().endsWith("pom.xml")){
    			pomFiles.add(file);
    		}else if (file.isDirectory()){
    			findAllPOMs(file, pomFiles);
    		}
    		
    	}
    }
    
    
    public static String codeToDescription(String code){
    	if(code.equals("noDepndancyTagsPattern")){
    		return "remove dependancy version tag from all sub modules and use dependancy management to define the versions";
    	} else if(code.equals("noSnapShotInDependancyPattern")){
    		return "SNAPSHOT dependencies can be only at the artifactVersion and for parent pom reference";
    	} else if(code.equals("noSnapShotInVersionPattern")){
    		return "SNAPSHOT dependencies can be only at the artifactVersion and for parent pom reference";
    	} else if(code.equals("noSnapShotInImportExportPackagePattern")){
    		return "OSGI import export package versions must not have any SNAPSHOTs";
    	} else if(code.equals("removedGroupIDFound")){
    		return "Check and make sure you have incorporate the group ID changes. Most has changed as given in https://docs.google.com/a/wso2.com/spreadsheets/d/1C0yy1Kj0d_ZAbmQuTJ1Y8udSeKVq8zCDalLXYwSrvbg/edit#gid=0";
    	} else if(code.equals("NotAllowedPropertyFound")){
    		return "wso2carbon.version, project.version, carbon.version, carbon.platform.version should not be used. Instead, you should define a property at the parent pom of the repo and used for all sub modules ";
    	}else{
    		return null;
    	}
    }
    
    
    public static void main(String[] args) throws Exception {
    	
    	File dir = new File(args[0]);
    	
    	List<File> pomFiles = new ArrayList<File>();
    	findAllPOMs(dir, pomFiles);
    	
    	for(File file: pomFiles){
    		checkPOM(file.getAbsolutePath().replace(dir.getAbsolutePath(), ""), Utils.readFile(file.getAbsolutePath()));
    		
    	}
    	
    	for( Entry<String, List<POMWarnBean>> errorType: wranningsList.entrySet()){
    		if(errorType.getValue().size() > 0){
        		System.out.println(errorType.getKey() + " ("+ codeToDescription(errorType.getKey()) + ")");
        		System.out.println("====================================");
        		for(POMWarnBean bean: errorType.getValue()){
        			if(bean.matches != null){
        				System.out.println(bean.path+ ":" + bean.matches);	
        			}else{
        				System.out.println(bean.path+ ":" + bean.l2Group);
        			}
        			
        		}
    		}
    	}
    }
}
