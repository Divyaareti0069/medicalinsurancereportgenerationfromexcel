FROM openjdk:17
EXPOSE 8081
ADD target/medicalinsurance-aws-exe.jar medicalinsurance-aws-exe.jar
ENTRYPOINT ["java", "-jar", "medicalinsurance-aws-exe.jar"]
