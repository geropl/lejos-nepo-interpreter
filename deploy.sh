#!/bin/bash

# NEPO Interpreter Complete Deployment Script
# Builds, uploads, and tests the NEPO interpreter on NXT

echo "🚀 NEPO Interpreter Complete Deployment"
echo "======================================="

# Check prerequisites
echo "🔍 Checking prerequisites..."

if ! command -v nxjc &> /dev/null; then
    echo "❌ ERROR: leJOS NXT not found!"
    echo "Please install leJOS NXT: http://www.lejos.org/nxt.php"
    exit 1
fi

if ! command -v nxj &> /dev/null; then
    echo "❌ ERROR: nxj command not found!"
    echo "Please ensure leJOS NXT is properly installed"
    exit 1
fi

echo "✅ leJOS NXT tools found"

# Build the interpreter
echo ""
echo "🔨 Building NEPO interpreter..."
./build.sh

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Cannot proceed with deployment."
    exit 1
fi

# Check NXT connection
echo ""
echo "🔌 Checking NXT connection..."
nxj -r HelloWorld > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "⚠️  WARNING: Cannot connect to NXT"
    echo "Please ensure:"
    echo "  - NXT is connected via USB"
    echo "  - NXT is powered on"
    echo "  - leJOS firmware is installed"
    echo ""
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Deployment cancelled."
        exit 1
    fi
else
    echo "✅ NXT connection verified"
fi

# Upload interpreter
echo ""
echo "📤 Uploading NEPO interpreter to NXT..."
nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain

if [ $? -ne 0 ]; then
    echo "❌ Failed to upload interpreter!"
    exit 1
fi

echo "✅ Interpreter uploaded successfully"

# Create example programs directory if it doesn't exist
mkdir -p examples

# Create example programs
echo ""
echo "📝 Creating example programs..."

# Hello World example
cat > examples/hello_world.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robActions_display_text" id="2">
          <value name="OUT">
            <block type="text" id="3">
              <field name="TEXT">Hello NEPO!</field>
            </block>
          </value>
          <next>
            <block type="robControls_wait_time" id="4">
              <value name="TIME">
                <block type="math_number" id="5">
                  <field name="NUM">3000</field>
                </block>
              </value>
            </block>
          </next>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
EOF

# Motor test example
cat > examples/motor_test.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robActions_display_text" id="2">
          <value name="OUT">
            <block type="text" id="3">
              <field name="TEXT">Motor Test</field>
            </block>
          </value>
          <next>
            <block type="robActions_motor_on" id="4">
              <field name="MOTORPORT">B</field>
              <field name="MOTORROTATION">ROTATIONS</field>
              <value name="POWER">
                <block type="math_number" id="5">
                  <field name="NUM">50</field>
                </block>
              </value>
              <value name="VALUE">
                <block type="math_number" id="6">
                  <field name="NUM">2</field>
                </block>
              </value>
            </block>
          </next>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
EOF

# Sensor test example
cat > examples/sensor_test.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robControls_repeat_times" id="2">
          <value name="TIMES">
            <block type="math_number" id="3">
              <field name="NUM">5</field>
            </block>
          </value>
          <statement name="DO">
            <block type="robActions_display_text" id="4">
              <value name="OUT">
                <block type="robSensors_touch_isPressed" id="5">
                  <field name="SENSORPORT">1</field>
                </block>
              </value>
              <next>
                <block type="robControls_wait_time" id="6">
                  <value name="TIME">
                    <block type="math_number" id="7">
                      <field name="NUM">1000</field>
                    </block>
                  </value>
                </block>
              </next>
            </block>
          </statement>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
EOF

echo "✅ Created 3 example programs"

# Upload example programs
echo ""
echo "📤 Uploading example programs..."

# Upload hello world as default program
nxj -upload examples/hello_world.xml test_program.xml
if [ $? -eq 0 ]; then
    echo "✅ hello_world.xml → test_program.xml (default)"
else
    echo "⚠️  Failed to upload hello_world.xml"
fi

# Upload other examples
nxj -upload examples/motor_test.xml
if [ $? -eq 0 ]; then
    echo "✅ motor_test.xml uploaded"
else
    echo "⚠️  Failed to upload motor_test.xml"
fi

nxj -upload examples/sensor_test.xml
if [ $? -eq 0 ]; then
    echo "✅ sensor_test.xml uploaded"
else
    echo "⚠️  Failed to upload sensor_test.xml"
fi

# Final status
echo ""
echo "🎉 Deployment completed!"
echo "======================="
echo ""
echo "📋 What's on your NXT:"
echo "  • NepoInterpreterMain - The NEPO interpreter"
echo "  • test_program.xml - Hello World program (runs by default)"
echo "  • motor_test.xml - Motor test program"
echo "  • sensor_test.xml - Sensor test program"
echo ""
echo "🎯 How to run:"
echo "  1. On NXT: Files → NepoInterpreterMain → Enter"
echo "  2. Program will load test_program.xml automatically"
echo "  3. Watch 'Hello NEPO!' appear on screen"
echo ""
echo "🔧 To run other programs:"
echo "  nxj -cp build -r NepoInterpreterMain motor_test.xml"
echo "  nxj -cp build -r NepoInterpreterMain sensor_test.xml"
echo ""
echo "📚 Next steps:"
echo "  • Create programs in OpenRoberta Lab (https://lab.open-roberta.org)"
echo "  • Export as XML and upload: nxj -upload your_program.xml"
echo "  • Run with: nxj -cp build -r NepoInterpreterMain your_program.xml"
echo ""
echo "🚀 Your NXT is ready for NEPO programming!"
