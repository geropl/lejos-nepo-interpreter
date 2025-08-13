FROM mcr.microsoft.com/devcontainers/universal:3.0.3

# Install leJOS NXJ for NXT development
USER root

# Install required dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    tar \
    openjdk-8-jdk \
    libusb-0.1-4 \
    libusb-dev \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME for Java 8 (required for leJOS NXJ)
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Create leJOS installation directory
RUN mkdir -p /opt/lejos_nxj

# Download and install leJOS NXJ 0.9.1beta-3
WORKDIR /tmp
RUN wget -q https://sourceforge.net/projects/nxt.lejos.p/files/0.9.1beta-3/leJOS_NXJ_0.9.1beta-3.tar.gz/download -O leJOS_NXJ_0.9.1beta-3.tar.gz \
    && tar -xzf leJOS_NXJ_0.9.1beta-3.tar.gz \
    && mv leJOS_NXJ_0.9.1beta-3/* /opt/lejos_nxj/ \
    && rm -rf /tmp/leJOS_NXJ_0.9.1beta-3* \
    && chmod +x /opt/lejos_nxj/bin/*

# Set leJOS environment variables
ENV NXJ_HOME=/opt/lejos_nxj
ENV PATH="$NXJ_HOME/bin:$PATH"
ENV CLASSPATH="$NXJ_HOME/lib/classes.jar:$NXJ_HOME/lib/jtools.jar:$CLASSPATH"

# Create symbolic links for common commands
RUN ln -sf /opt/lejos_nxj/bin/nxjc /usr/local/bin/nxjc \
    && ln -sf /opt/lejos_nxj/bin/nxjlink /usr/local/bin/nxjlink \
    && ln -sf /opt/lejos_nxj/bin/nxjupload /usr/local/bin/nxjupload \
    && ln -sf /opt/lejos_nxj/bin/nxj /usr/local/bin/nxj

# Set up USB permissions for NXT (for when USB passthrough is available)
RUN mkdir -p /etc/udev/rules.d && echo 'SUBSYSTEM=="usb", ATTR{idVendor}=="0694", ATTR{idProduct}=="0002", MODE="0666", GROUP="plugdev"' > /etc/udev/rules.d/70-lego-nxt.rules

# Switch back to codespace user
USER codespace

# Verify installation
RUN echo "leJOS NXJ installation complete" \
    && echo "NXJ_HOME: $NXJ_HOME" \
    && echo "Java version:" \
    && java -version \
    && echo "leJOS tools available:" \
    && ls -la /opt/lejos_nxj/bin/

WORKDIR /workspaces
