FROM jenkins/jenkins:2.86
USER jenkins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/plugins.txt
COPY groovy/*.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY groovy/*.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY jobs /var/jenkins_home/jobs
