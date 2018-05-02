import jenkins.*
import jenkins.model.*
import hudson.model.*
import hudson.model.labels.*
import org.jenkinsci.plugins.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import javaposse.jobdsl.plugin.*
import hudson.plugins.git.*
import hudson.plugins.git.extensions.*


def jenkins = Jenkins.instance
def env = System.getenv()

String gitCredentialId = env['JENKINS_GIT_CREDENTIAL_ID']
String gitDescription = 'GitHub Git Account'
String gitUsername = env['JENKINS_GIT_USERNAME']
String gitPassword = env['JENKINS_GIT_PASSWORD']
String gitUrl = env['JENKINS_GIT_URL']
String gitBranchSpec = env['JENKINS_GIT_BRANCH_SPEC']

//disable script approval security for JobDSL scripts
def jobDslSecurity = jenkins.getDescriptor('javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration')
jobDslSecurity.useScriptSecurity = false
jobDslSecurity.save()

if(gitCredentialId != '')
{
  def credentialDomain = Domain.global()
  def credentialStore = jenkins.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
  
  
  //Create/Update credentials for the Git accounts
  
  gitCredentials = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    gitCredentialId, gitDescription,
    gitUsername,
    gitPassword
  )
  
  def allCredentials = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, Jenkins.instance)
  def currentCredentials = allCredentials.findResult { it.id == gitCredentialId ? it : null }
  
  if(currentCredentials)
  {
    print('Updating Git account credentials... ')
    credentialStore.updateCredentials(credentialDomain, currentCredentials, gitCredentials)
    println('OK')
  }
  else
  {
    print('Adding Git account credentials... ')
    credentialStore.addCredentials(credentialDomain, gitCredentials)
    println('OK')
  }
  
  def seedJobName = 'Seed_the_World'
  def seedJobDisplayName = 'Seed the World'
  def seedDslScriptPath = 'jenkins/job-dsl/main.groovy'
  
  def seedDsl = new ExecuteDslScripts()
  seedDsl.setTargets(seedDslScriptPath)
  seedDsl.setRemovedJobAction(RemovedJobAction.DELETE)
  seedDsl.setRemovedViewAction(RemovedViewAction.DELETE)
  
  def seedGitRepo = GitSCM.createRepoList(gitUrl, gitCredentialId)
  def seedBranchSpec = [new BranchSpec(gitBranchSpec)]
  def seedGitScm = new GitSCM(seedGitRepo, seedBranchSpec, false, [], null, null, [])
  
  
  def seedJob = jenkins.getItem(seedJobName)
  
  
  if(!seedJob)
  {
    seedJob = new FreeStyleProject(jenkins, seedJobName)
    jenkins.add(seedJob, seedJobName)
  }
  
  seedJob.setDisplayName('Seed the World')
  seedJob.setAssignedLabel(new LabelAtom('master'))
  seedJob.scm = seedGitScm
  seedJob.getBuildersList().replace(seedDsl)

}

