FROM ubuntu:latest
# Install necessary packages
RUN apt-get update && apt-get install -y git bash

RUN apt-get install openjdk-17-jdk -y

RUN apt-get install maven -y

# Set the working directory
WORKDIR /home/app/

# Copy your script
COPY . .

# Make sure the script is executable
RUN chmod +x /home/app/main.sh


# Run the script
CMD ["/home/app/main.sh"]
