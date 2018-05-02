import jenkins.model.*
import hudson.model.*
import hudson.markup.*

println('**** Configuring Settings of Jenkins Master ****\n')

def jenkins = Jenkins.instance

jenkins.setLabelString('master')
jenkins.setMode(Node.Mode.EXCLUSIVE)

jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE)

def jenkinsUrl = System.getenv('JENKINS_URL')
def jlc = JenkinsLocationConfiguration.get()
  
jlc.setUrl(jenkinsUrl)
jlc.save()

