#version 430

mat4 buildRotateZ(float rad) {
	mat4 zrot = mat4(cos(rad), -sin(rad), 0.0, 0.0,
	sin(rad), cos(rad), 0.0, 0.0,
	0.0, 0.0, 1.0, 0.0,
	0.0, 0.0, 0.0, 1.0);

	return zrot;
}

uniform float rads;

void main() {
	mat4 rot = buildRotateZ(rads);
	if (gl_VertexID == 0)
		gl_Position = vec4(0.25, -0.25, 0.0, 1.0);
	else if (gl_VertexID == 1)
		gl_Position = vec4(-0.25, -0.25, 0.0, 1.0);
	else
		gl_Position = vec4(0.25, 0.25, 0.0, 1.0);

	gl_Position = rot * gl_Position;
}


