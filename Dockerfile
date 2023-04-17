FROM nunopreguica/sd2223tpbase

# working directory inside docker image
WORKDIR /home/sd

# copy the jar created by assembly to the docker image
COPY target/*jar-with-dependencies.jar sd2223.jar

# copy the file of properties to the docker image
COPY feeds.props feeds.props

# run Discovery when starting the docker image
#CMD ["java", "-cp", "sd2223.jar", "sd2223.trab1.server.UsersServer", "nova"]

