FROM socrata/java8

EXPOSE 1989

ENV SERVER_ROOT /srv/image-resize-service
ENV SERVER_ARTIFACT image-resize-service-assembly.jar

# general secondary-watcher defaults
ENV JAVA_XMX 512m

# Java 7 PermGen default.
ENV COMPRESSED_CLASS_SIZE 64m
# Compressed_class_size + Java 7 Permgen default.
ENV MAX_META_SIZE 128m

WORKDIR $SERVER_ROOT

COPY ship.d /etc/ship.d
COPY $SERVER_ARTIFACT $SERVER_ROOT/
