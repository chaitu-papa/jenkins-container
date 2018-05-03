import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.*
import jenkins.security.s2m.AdminWhitelistRule

println('**** Configuring admin accoutn ****\n')

def env = System.getenv()

String admin = env['JENKINS_ADMIN']
String adminPassword = env['JENKINS_ADMIN_PASSWORD']

if(admin != '')
{
  def instance = Jenkins.getInstance() 
  def hudsonRealm = new HudsonPrivateSecurityRealm(false)
  hudsonRealm.createAccount(admin, adminPassword)
  instance.setSecurityRealm(hudsonRealm)
  def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
  instance.setAuthorizationStrategy(strategy)
  Jenkins.instance.save()
  Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
}
