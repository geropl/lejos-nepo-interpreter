# Core NEPO Block Types for NXT Implementation

## Essential Blocks for Basic Functionality

### Control Flow Blocks
- `robControls_start` - Program entry point
- `robControls_wait_time` - Wait/delay functionality
- `robControls_if` - Conditional execution
- `robControls_ifElse` - If-else conditional
- `robControls_repeat_times` - Repeat loop with count
- `robControls_repeat_forever` - Infinite loop
- `robControls_while` - While loop with condition

### Action Blocks (Motors)
- `robActions_motor_on` - Start motor with power/rotation
- `robActions_motor_stop` - Stop motor
- `robActions_motor_getPower` - Get current motor power
- `robActions_motorDiff_on` - Differential drive (both motors)
- `robActions_motorDiff_stop` - Stop differential drive

### Action Blocks (Display/Sound)
- `robActions_display_text` - Show text on LCD
- `robActions_display_clear` - Clear LCD display
- `robActions_play_tone` - Play tone/beep
- `robActions_play_note` - Play musical note

### Sensor Blocks
- `robSensors_touch_isPressed` - Touch sensor state
- `robSensors_ultrasonic_distance` - Ultrasonic distance
- `robSensors_light_light` - Light sensor value
- `robSensors_sound_loud` - Sound sensor value
- `robSensors_gyro_angle` - Gyro sensor angle (if available)
- `robSensors_timer_get` - Get timer value
- `robSensors_timer_reset` - Reset timer

### Logic Blocks
- `logic_boolean` - Boolean true/false values
- `logic_compare` - Comparison operations (==, !=, <, >, etc.)
- `logic_operation` - Logical operations (AND, OR, NOT)

### Math Blocks
- `math_number` - Numeric constants
- `math_arithmetic` - Basic arithmetic (+, -, *, /, %)
- `math_single` - Single operand math (abs, sqrt, etc.)
- `math_random_int` - Random integer generation

### Text Blocks
- `text` - Text/string constants
- `text_join` - String concatenation

### Variable Blocks
- `variables_get` - Get variable value
- `variables_set` - Set variable value

## Block Priority for Implementation

### Phase 1 (Minimal Working Program)
1. `robControls_start`
2. `robActions_display_text`
3. `robActions_motor_on`
4. `robActions_motor_stop`
5. `robControls_wait_time`
6. `math_number`
7. `text`

### Phase 2 (Basic Interactivity)
8. `robSensors_touch_isPressed`
9. `robSensors_ultrasonic_distance`
10. `robControls_if`
11. `logic_compare`
12. `logic_boolean`

### Phase 3 (Full Control Flow)
13. `robControls_repeat_times`
14. `robControls_while`
15. `variables_get`
16. `variables_set`
17. `math_arithmetic`

### Phase 4 (Advanced Features)
18. `robSensors_light_light`
19. `robSensors_sound_loud`
20. `robActions_play_tone`
21. `robActions_motorDiff_on`
22. `text_join`

## Block XML Structure Examples

### Motor On Block
```xml
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
      <field name="NUM">1</field>
    </block>
  </value>
</block>
```

### Touch Sensor Block
```xml
<block type="robSensors_touch_isPressed" id="8">
  <field name="SENSORPORT">1</field>
</block>
```

### If Block
```xml
<block type="robControls_if" id="9">
  <value name="IF0">
    <block type="robSensors_touch_isPressed" id="10">
      <field name="SENSORPORT">1</field>
    </block>
  </value>
  <statement name="DO0">
    <block type="robActions_display_text" id="11">
      <value name="OUT">
        <block type="text" id="12">
          <field name="TEXT">Button pressed!</field>
        </block>
      </value>
    </block>
  </statement>
</block>
```

### Repeat Block
```xml
<block type="robControls_repeat_times" id="13">
  <value name="TIMES">
    <block type="math_number" id="14">
      <field name="NUM">5</field>
    </block>
  </value>
  <statement name="DO">
    <block type="robActions_play_tone" id="15">
      <value name="FREQUENCY">
        <block type="math_number" id="16">
          <field name="NUM">440</field>
        </block>
      </value>
      <value name="DURATION">
        <block type="math_number" id="17">
          <field name="NUM">500</field>
        </block>
      </value>
    </block>
  </statement>
</block>
```

## Hardware Mapping

### Motor Ports
- NEPO: "A", "B", "C"
- leJOS: Motor.A, Motor.B, Motor.C

### Sensor Ports
- NEPO: "1", "2", "3", "4"
- leJOS: SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4

### Sensor Types
- Touch: TouchSensor class
- Ultrasonic: UltrasonicSensor class
- Light: LightSensor class
- Sound: SoundSensor class
