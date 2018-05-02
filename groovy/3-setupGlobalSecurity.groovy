import jenkins.model.*
import hudson.model.*
import hudson.security.*
import jenkins.security.s2m.*
import com.cloudbees.plugins.credentials.*
import hudson.PluginManager.*
import hudson.scm.*

def env = System.getenv()
def jenkins = Jenkins.getInstance()
def strategy = new ProjectMatrixAuthorizationStrategy()

//Set Slave -> Master security
jenkins.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

//Disable CLI Remoting
jenkins.getDescriptor(jenkins.CLI.class).setEnabled(false)

  
def adminList = env['JENKINS_ADMINS'].tokenize(',')*.trim()

adminList.each { username ->
	strategy.add(Jenkins.ADMINISTER, username)
}

def slaveManagerList = env['JENKINS_NODE_MANAGERS'].tokenize(',')*.trim()

slaveManagerList.each { username ->
	strategy.add(Computer.CONFIGURE, username)
	strategy.add(Computer.BUILD, username)
	strategy.add(Computer.CONNECT, username)
	strategy.add(Computer.DELETE, username)
	strategy.add(Computer.DISCONNECT, username)
	strategy.add(Computer.CREATE, username)
}

//Allow Anonymous users to see the main dashboard w/o authenticating
strategy.add(Jenkins.READ, 'anonymous')

/* Reference for future permissions 

overallManagerList = ['CredTest']

overallManagerList.each { username ->
	strategy.add(Jenkins.READ, username)
	strategy.add(Jenkins.RUN_SCRIPTS, username)
	strategy.add(PluginManager.UPLOAD_PLUGINS, username)
	strategy.add(PluginManager.CONFIGURE_UPDATECENTER, username)
}

credManagerList = ['CredTest']

credManagerList.each { username ->
	strategy.add(CredentialsProvider.DELETE, username)
	strategy.add(CredentialsProvider.CREATE, username)
	strategy.add(CredentialsProvider.MANAGE_DOMAINS, username)
	strategy.add(CredentialsProvider.UPDATE, username)
	strategy.add(CredentialsProvider.VIEW, username)
}

jobManagerList = ['CredTest']

jobManagerList.each { username ->
	strategy.add(Item.BUILD, username)
	strategy.add(Item.CANCEL, username)
	strategy.add(Item.CONFIGURE, username)
	strategy.add(Item.CREATE, username)
	strategy.add(Item.DELETE, username)
								Find static move permission 
	strategy.add(Item.DISCOVER, username)
	strategy.add(Item.READ, username)
	strategy.add(Item.WORKSPACE, username)
}

runManagerList = ['CredTest']

runManagerList.each { username ->
	strategy.add(Run.DELETE, username)
	strategy.add(Run.UPDATE, username)
}

viewManagerList = ['CredTest']

viewManagerList.each { username ->
	strategy.add(View.DELETE, username)
	strategy.add(View.CONFIGURE, username)
	strategy.add(View.READ, username)
	strategy.add(View.CREATE, username)
}

scmManagerList = ['CredTest']

scmManagerList.each { username ->
	strategy.add(scm.SCM.TAG, username)
}

*/

System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")

if(!strategy.getAllSIDs().empty)
{
  jenkins.setAuthorizationStrategy(strategy)
}
else
{
  jenkins.setAuthroizationStrategy(AuthorizationStrategy.Unsecured)
}

jenkins.save()

