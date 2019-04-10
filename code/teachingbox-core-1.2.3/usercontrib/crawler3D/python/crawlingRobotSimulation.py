# crawlingRobotSimulation.py
# A physical simulation of the crawling robot (ailab.hs-weingarten.de)
# Author: Markus Schips, 2010

#physic-engine: Open Dynamics Engine (pyode)
#visualisation: OpenGL (pyopengl)
#communication: xmlrpclib
#configuration: configobj
#userinterface: pyui, pygame

import pyui, sys, os, random, time, math, socket
from math import *

import pygame
from pygame.locals import *

from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *

from collections import deque

import ode

from configobj import ConfigObj
go = False
visualize = False;

config = ConfigObj("conf", list_values=True)

glutInit ([])

# Navigation
rotX = 0.0
rotY = 0.0
rotZ = 0.0

centerX = 0.0
centerY = 0.0
centerZ = 0.0

zoom = 1.0

mouseOldXPos = 0.0
mouseOldYPos = 0.0

oldPos = 0.0
reward = 0.0
maxReward = 0.0
rewardValues = deque(maxlen=50)

"""Parameter"""
floorAngleDeg = 0.0
floorAngle = math.radians(floorAngleDeg)
lastFloorAngle = floorAngle
planeLength = 1000.0
planeWidth = 5.0

#Default
armSpeed = 1.0
armPower = 3000
simulationSpeed = 1

mu_foot = 5000
mu_arm = 5000
mu_wheels = 5000
mu_floor = 2000

damping = 0.0002
spring = 0.1

resolutionWidth = 800
resolutionHeight = 600
try:
    resolutionWidth = int(config['settings']['resolutionWidth'])
    resolutionHeight = int(config['settings']['resolutionHeight'])
except:
    pass

screenWidth = resolutionWidth
screenHeight = resolutionHeight

try:
    armSpeed = float(config['settings']['armSpeed'])
    armPower = float(config['settings']['armPower'])
    simulationSpeed = float(config['settings']['simulationSpeed'])
    mu_foot = float(config['settings']['mu_foot'])
    mu_arm = float(config['settings']['mu_arm'])
    mu_floor = float(config['settings']['mu_floor'])
    mu_wheels = float(config['settings']['mu_wheels'])
    damping = float(config['settings']['damping'])
    spring = float(config['settings']['spring'])
except:
    pass

"""ODE"""
world = None
space = None
bodies = None
floorX = None
floor = None
contactgroup = None
fps = None
dt = None
running = True
state = None
counter = None
objcount = None
lasttime = None

#Corpus_Robotus
corpus_robotus = None

corpus_robotus_xDefault = 2.0
corpus_robotus_yDefault = 0.3
corpus_robotus_zDefault = 1.5
corpus_robotus_densityDefault = 1000
corpus_robotus_groundClearanceDefault = 0.15

corpus_robotus_x = corpus_robotus_xDefault
corpus_robotus_y = corpus_robotus_yDefault
corpus_robotus_z = corpus_robotus_zDefault
corpus_robotus_density = corpus_robotus_densityDefault
corpus_robotus_groundClearance = corpus_robotus_groundClearanceDefault

wheelRadius = None

#Wheels
wheelLeft = None
jointWheelLeft = None
wheelRight= None
jointWheelRight = None

wheel_radiusDefault = 0.2
wheel_lengthDefault = 0.2
wheel_densityDefault = 250

wheel_radius = wheel_radiusDefault
wheel_length = wheel_lengthDefault
wheel_density = wheel_densityDefault

#Foot
foot = None
jointFoot = None

foot_xDefault = 0.1
foot_yDefault = 0.1
foot_zDefault = 1.5
foot_densityDefault = 250

foot_x = foot_xDefault
foot_y = foot_yDefault
foot_z = foot_zDefault
foot_density = foot_densityDefault

#Arm 1
arm1 = None
jointArm1 = None

arm1_xDefault = 0.1
arm1_yDefault = 1.0
arm1_zDefault = 0.4
arm1_densityDefault = 250
arm1_edgeDistanceDefault = 0.2
arm1_hingeHeightDefault = 0.05

arm1_x = arm1_xDefault
arm1_y = arm1_yDefault
arm1_z = arm1_zDefault
arm1_density = arm1_densityDefault
arm1_edgeDistance = arm1_edgeDistanceDefault
arm1_hingeHeight = arm1_hingeHeightDefault = 0.05

#Arm 2
arm2 = None
jointArm2 = None

arm2_xDefault = 0.1
arm2_yDefault = 1.0
arm2_zDefault = 0.4
arm2_densityDefault = 250
arm2_hingeHeightDefault = 0.05

arm2_x = arm2_xDefault
arm2_y = arm2_yDefault
arm2_z = arm2_zDefault
arm2_density = arm2_densityDefault
arm2_hingeHeight = arm2_hingeHeightDefault

#Logo
logoFilePathDefault = 'images/logo.png'

try:
    corpus_robotus_x = float(config['robot']['corpus_robotus_x'])
    corpus_robotus_y = float(config['robot']['corpus_robotus_y'])
    corpus_robotus_z = float(config['robot']['corpus_robotus_z'])
    corpus_robotus_density = float(config['robot']['corpus_robotus_density'])
    corpus_robotus_groundClearance = float(config['robot']['corpus_robotus_groundClearance'])
    
    wheel_radius = float(config['robot']['wheel_radius'])
    wheel_length = float(config['robot']['wheel_length'])
    wheel_density = float(config['robot']['wheel_density'])

    foot_x = float(config['robot']['foot_x'])
    foot_y = float(config['robot']['foot_y'])
    foot_z = float(config['robot']['foot_z'])
    foot_density = float(config['robot']['foot_density'])

    arm1_x = float(config['robot']['arm1_x'])
    arm1_y = float(config['robot']['arm1_y'])
    arm1_z = float(config['robot']['arm1_z'])
    arm1_density = float(config['robot']['arm1_density'])
    arm1_edgeDistance = float(config['robot']['arm1_edgeDistance'])
    arm1_hingeHeight = float(config['robot']['arm1_hingeHeight'])

    arm2_x = float(config['robot']['arm2_x'])
    arm2_y = float(config['robot']['arm2_y'])
    arm2_z = float(config['robot']['arm2_z'])
    arm2_density = float(config['robot']['arm2_density'])
    arm2_hingeHeight = float(config['robot']['arm2_hingeHeight'])
except:
    pass

amountOfObstacles = 7
distanceOfObstacles = 1.0
dimension_xOfObstacles = 0.3
dimension_yOfObstacles = 0.1
dimension_zOfObstacles = 2.0
densityOfObstacles = 250.0
fixObstacles = 0
startingDistance = 2.0

try:
    amountOfObstacles = int(config['obstacles']['amount'])
    distanceOfObstacles = float(config['obstacles']['distance'])
    dimension_xOfObstacles = float(config['obstacles']['dimensionX'])
    dimension_yOfObstacles = float(config['obstacles']['dimensionY'])
    dimension_zOfObstacles = float(config['obstacles']['dimensionZ'])
    densityOfObstacles = float(config['obstacles']['density'])
    fixObstacles = int(config['obstacles']['fixObstacles'])
    startingDistance = float(config['obstacles']['startingDistance'])
except:
    pass

Arm1PositionsDefault = 0, 30, 50, 70, 90
Arm2PositionsDefault = 90, 70, 50, 30, 0

Arm1Positions = Arm1PositionsDefault
Arm2Positions = Arm2PositionsDefault

try:
    Arm1Positions =  [float(integral) for integral in config['settings']['arm1Positions']]
    Arm2Positions =  [float(integral) for integral in config['settings']['arm2Positions']]
except:
    pass


initialArmState = 5,1
armState = initialArmState

visualize = 0
try:
    visualize =  int(config['settings']['visualize'])
except:
    pass

autoCameraMovement = 0
try:
    autoCameraMovement =  int(config['settings']['autoCameraMovement'])
except:
    pass

showRewardValues = 0
try:
    showRewardValues =  int(config['settings']['showRewardValues'])
except:
    pass

bodyForColorChanging = None
leftMouseButtonPressed = False;

drawLogo = True
logoFilePath = logoFilePathDefault
try:
    if os.path.exists(config['logo']['logoFilePath']):
        logoFilePath = config['logo']['logoFilePath']
except:
    print "loading default Logo"


"""DefaultColor"""

"""
corpus_robotusDefaultColor = 0.2, 0.2, 0.2
corpus_robotusDefaultColor = 0.2, 0.2, 0.2
wheelLeftDefaultColor = 0.2, 0.2, 0.2
wheelRightDefaultColor = 0.2, 0.2, 0.2
arm1DefaultColor = 0.2, 0.2, 0.2
arm2DefaultColor = 0.2, 0.2, 0.2
footDefaultColor = 0.2, 0.2, 0.2
"""

# prepare_GL
def prepare_GL():
    global screenWidth, screenHeight

    """Prepare drawing"""
    # Viewport
    pyui.desktop.getRenderer().clear()

    # Initialize
    glClearColor(0.8,0.8,0.9,0)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_LIGHTING)
    glEnable(GL_NORMALIZE)
    glShadeModel(GL_SMOOTH)
    glEnable(GL_COLOR_MATERIAL)

    # Projection
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluPerspective (45,1.3333,0.2,500)

    # Initialize ModelView matrix
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()

    # Light source
    
    glLightfv(GL_LIGHT0,GL_POSITION,[0,0,1,0])
    glLightfv(GL_LIGHT0,GL_DIFFUSE,[1,1,1,1])
    glLightfv(GL_LIGHT0,GL_SPECULAR,[1,1,1,1])
    glEnable(GL_LIGHT0)
    
    # View transformation
    gluLookAt (0.0, 3.0, 50.8, 0, 1, 0, 0, 1, 0)

    if showRewardValues:
        glPushMatrix()
        glTranslate(-6,-1.5,40)
        for i in range(0,len(rewardValues)):
            if rewardValues[i] > 0:
                glColor4f(0.0,1.0,0.0,1.0)
            else:
                if rewardValues[i] < 0:
                    glColor4f(1.0,0.0,0.0,1.0)
                else:
                    glColor4f(1.0,1.0,1.0,1.0)
            glTranslate(0.1,rewardValues[i],0)
            glutSolidSphere(0.02, 10, 10)
            glTranslate(0,-rewardValues[i],0)
        glPopMatrix()

    glRotatef(rotX,1.0,0.0,0.0); # Rotate on x
    glRotatef(rotY,0.0,1.0,0.0); # Rotate on y
    glRotatef(rotZ,0.0,0.0,1.0); # Rotate on z

    glScale(zoom, zoom, zoom)
    glTranslate(-centerX,-centerY,-centerZ)


def draw_street():
    # Street
    try:
        Surface = pygame.image.load('images/street.png')

        Data = pygame.image.tostring(Surface, "RGBA", 1)
        glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, Surface.get_width(), Surface.get_height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, Data )
                        
        glDisable(GL_LIGHTING)
        glPushMatrix()
        glColor4f(1,1,1,1)
        glEnable(GL_TEXTURE_2D)

        glTexParameterf (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameterf (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

        glBegin(GL_QUADS)
        glTexCoord2f(0.0,0.0); glVertex3f(-planeLength, -planeLength*tan(floorAngle), planeWidth)
        glTexCoord2f(0.0,100.0); glVertex3f( planeLength, planeLength*tan(floorAngle), planeWidth)
        glTexCoord2f(1.0,100.0); glVertex3f( planeLength, planeLength*tan(floorAngle),-planeWidth)
        glTexCoord2f(1.0,0.0); glVertex3f(-planeLength, -planeLength*tan(floorAngle),-planeWidth)
        glEnd()

        glDisable(GL_TEXTURE_2D)
        glPopMatrix()
        glEnable(GL_LIGHTING)
    except:
        pass

def draw_overlay():
    try:
        Surface = pygame.image.load('images/hs.png')

        Data = pygame.image.tostring(Surface, "RGBA", 1)
        glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, Surface.get_width(), Surface.get_height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, Data )

        glColor4f(1,1,1,0.75)
        glEnable(GL_TEXTURE_2D)

        glMatrixMode(GL_PROJECTION)
        glPushMatrix()
        glLoadIdentity()

        gluOrtho2D(0, screenWidth, 0, screenHeight)
        glMatrixMode(GL_MODELVIEW)
        glPushMatrix()
        glLoadIdentity()
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_LIGHTING)

        glTexParameterf (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameterf (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

        glBegin(GL_QUADS)
        glTexCoord2f(0,0);glVertex2f(screenWidth-300, 10)
        glTexCoord2f(0,1);glVertex2f(screenWidth-300, 116)
        glTexCoord2f(1,1);glVertex2f(screenWidth-10, 116)
        glTexCoord2f(1,0);glVertex2f(screenWidth-10, 10)
        glEnd()

        glMatrixMode(GL_PROJECTION)
        glPopMatrix()
        glMatrixMode(GL_MODELVIEW)
        glPopMatrix()
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_LIGHTING)
        glDisable(GL_TEXTURE_2D)
    except:
        pass

# draw_body
def draw_body(body):
    global corpus_red, corpus_green, corpus_blue, drawLogo
    """Draw an ODE body."""

    x,y,z = body.getPosition()
    R = body.getRotation()
    rot = [R[0], R[3], R[6], 0.,
           R[1], R[4], R[7], 0.,
           R[2], R[5], R[8], 0.,
           x, y, z, 1.0]
    glPushMatrix()
    glMultMatrixd(rot)
    glColor4f(0.2,0.4,0.3,1.0)
    glColor3f(body.color[0], body.color[1], body.color[2])
    if body.shape=="box":
        sx,sy,sz = body.boxsize
        glScale(sx, sy, sz)
        glutSolidCube(1)
	
        if body.name == "corpus_robotus":

            if drawLogo:
            
                try:
                    Surface = pygame.image.load(logoFilePath)
                    
                    Data = pygame.image.tostring(Surface, "RGBA", 1)
                    glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, Surface.get_width(), Surface.get_height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, Data )
                    
                    glDisable(GL_LIGHTING)
                    glPushMatrix()
                    glColor4f(1,1,1,1)
                    glEnable(GL_TEXTURE_2D)

                    glTexParameterf (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                    glTexParameterf (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

                    # Logo CorpusRobotus oben
                    glBegin(GL_QUADS)
                    glTexCoord2f(0,0);glVertex3f(0.15, 0.51, 0.3)
                    glTexCoord2f(0,1);glVertex3f(-0.15, 0.51, 0.3)
                    glTexCoord2f(1,1);glVertex3f(-0.15, 0.51, -0.3)
                    glTexCoord2f(1,0);glVertex3f(0.15, 0.51, -0.3)
                    glEnd()

                    # Logo CorpusRobotus links
                    glBegin(GL_QUADS)
                    glTexCoord2f(0,0);glVertex3f(-0.4, -0.2, 0.51)
                    glTexCoord2f(0,1);glVertex3f(-0.4, 0.3, 0.51)
                    glTexCoord2f(1,1);glVertex3f(-0.2, 0.3, 0.51)
                    glTexCoord2f(1,0);glVertex3f(-0.2, -0.2, 0.51)
                    glEnd()

                    # Logo auf CorpusRobotus rechts
                    glBegin(GL_QUADS)
                    glTexCoord2f(0,0);glVertex3f(-0.2, -0.2, -0.51)
                    glTexCoord2f(0,1);glVertex3f(-0.2, 0.3, -0.51)
                    glTexCoord2f(1,1);glVertex3f(-0.4, 0.3, -0.51)
                    glTexCoord2f(1,0);glVertex3f(-0.4, -0.2, -0.51)
                    glEnd()

                    ##########################
                    """
                    glColor4f(1,1,1,0.75);
                    glEnable(GL_TEXTURE_2D);

                    glMatrixMode(GL_PROJECTION);
                    glPushMatrix();
                    glLoadIdentity();

                    gluOrtho2D(0, 800, 0, 600);
                    glMatrixMode(GL_MODELVIEW);
                    glPushMatrix();
                    glLoadIdentity();
                    glDisable(GL_DEPTH_TEST);
                    glDisable(GL_LIGHTING);

                    glBegin(GL_QUADS);
                    glTexCoord2f(0,0);glVertex2f(800-120, 10);
                    glTexCoord2f(0,1);glVertex2f(800-120, 120);
                    glTexCoord2f(1,1);glVertex2f(800-10, 120);
                    glTexCoord2f(1,0);glVertex2f(800-10, 10);
                    glEnd();

                    glMatrixMode(GL_PROJECTION);
                    glPopMatrix();
                    glMatrixMode(GL_MODELVIEW);
                    glPopMatrix();
                    glEnable(GL_DEPTH_TEST);
                    glEnable(GL_LIGHTING);
                    glDisable(GL_TEXTURE_2D);
                    """


                    ##########################


                    glDisable(GL_TEXTURE_2D)
                    glPopMatrix()
                    glEnable(GL_LIGHTING)
                except:
                    drawLogo = False
                    print "could not load logo file " + logoFilePath
     
    if body.shape=="sphere":
        radius = body.radius
        glutSolidSphere(radius, 50, 50)
    if body.shape=="cylinder":
        radius = body.radius
        length = body.length
        q = gluNewQuadric()
        glTranslatef(0.0, 0.0, -length/2)
        gluCylinder(q, radius, radius, length, 50, 50)
        glTranslatef(0.0, 0.0, length/2)
        glTranslatef(0.0, 0.0, length/2)
        gluDisk(q, 0.0, radius, 50, 1)
        glTranslatef(0.0, 0.0, -length/2)
        glRotatef(180,1.0,0.0,0.0); # Rotate on x
        glTranslatef(0.0, 0.0, length/2)
        gluDisk(q, 0.0, radius, 50, 1)
        glTranslatef(0.0, 0.0, -length/2)
    glPopMatrix()
    
# create_box
def create_box(world, space, density, lx, ly, lz, name="box"):
    """Create a box body and its corresponding geom."""
    # Create body
    body = ode.Body(world)
    M = ode.Mass()
    M.setBox(density, lx, ly, lz)
    body.setMass(M)

    # Set parameters for drawing the body
    body.shape = "box"
    body.name = name
    body.boxsize = (lx, ly, lz)
    body.color = 0.2, 0.2, 0.2

    # Create a box geom for collision detection
    geom = ode.GeomBox(space, lengths=body.boxsize)
    geom.setBody(body)

    return body, geom

# create_sphere
def create_sphere(world, space, density, radius, name="sphere"):
    """Create a sphere body and its corresponding geom."""

    # Create body
    body = ode.Body(world)
    M = ode.Mass()
    M.setSphere(density, radius)
    body.setMass(M)

    # Set parameters for drawing the body
    body.shape = "sphere"
    body.name = name
    body.radius = radius
    body.color = 1.0, 1.0, 1.0
    
    # Create a sphere geom for collision detection
    geom = ode.GeomSphere(space, radius=body.radius)
    geom.setBody(body)

    return body, geom

# create_cylinder
def create_cylinder(world, space, density, radius, length, axis, name="cylinder"):
    """Create a sphere body and its corresponding geom."""

    # Create body
    body = ode.Body(world)
    M = ode.Mass()
    M.setCylinder(density, axis, radius, length)
    body.setMass(M)

    # Set parameters for drawing the body
    body.shape = "cylinder"
    body.name = name
    body.radius = radius
    body.length = length
    body.color = 0.2, 0.2, 0.2

    # Create a sphere geom for collision detection
    geom = ode.GeomCylinder(space, radius=body.radius, length=body.length)
    geom.setBody(body)

    return body, geom
    

# Collision callback
def collision_callback(args, geom1, geom2):
    global spring, damping, mu, mu_arm, mu_foot, mu_floor
    """Callback function for the collide() method.

    This function checks if the given geoms do collide and
    creates contact joints if they do.
    """

    # Check if the objects do collide
    contacts = ode.collide(geom1, geom2)
    
    # Create contact joints
    world,contactgroup = args
    for c in contacts:
        c.setMode(24)
        
        #c.setBounce(1.8)
    
        try:
            if geom1.getBody().name == "foot":
                c.setMu(mu_foot)
            else:
                if geom1.getBody().name == "arm2":
                    c.setMu(mu_arm)
                else:
                    c.setMu(mu_floor)
        except:
            c.setMu(mu_floor)
            
        c.setSoftERP(spring)
        c.setSoftCFM(damping)
        j = ode.ContactJoint(world, contactgroup, c)
        j.attach(geom1.getBody(), geom2.getBody())


def translation(body):
    global floorAngle, lastFloorAngle
    x,y,z = body.getPosition()
    
    if x == 0:
        object_angle = math.pi/2
    else:
        object_angle = (atan(y/x)) - lastFloorAngle

    radius_body = sqrt(pow(y,2) + pow(x,2) )
    if x < 0:
        radius_body = -radius_body

    xBody = radius_body * cos(floorAngle + object_angle)
    yBody = radius_body * sin(floorAngle + object_angle)

    body.setPosition((xBody,yBody,z))

def prepare_ODE():
    global world, space, bodies, geoms, floorX, floor, contactgroup, fps, dt, running, state, counter, objcount, lasttime, corpus_robotus, wheelLeft, jointWheelLeft, wheelRight, jointWheelRight, foot, jointFoot, arm1, jointArm1, arm2, jointArm2, pendulum
    # Create a world object
    world = ode.World()
    world.setGravity( (0,-9.81,0) )
    world.setERP(0.2)
    world.setCFM(1E-5)

    # Create a space object
    space = ode.Space()

    # A list with ODE bodies
    bodies = []

    # The geoms for each of the bodies
    geoms = []

    # Create a plane geom which prevent the objects from falling forever
    floorX = tan(floorAngle)
    floor = ode.GeomPlane(space, (-floorX,1,0), 0)

    # A joint group for the contact joints that are generated whenever
    # two bodies collide
    contactgroup = ode.JointGroup()

    # Some variables used inside the simulation loop
    fps = 50
    dt = 1.0/fps
    running = True
    state = 0
    counter = 0
    objcount = 0
    lasttime = time.time()

    #########Create Robot########################
    """Corpus Robotus"""
    corpus_robotus, geom = create_box(world, space, corpus_robotus_density, corpus_robotus_x, corpus_robotus_y, corpus_robotus_z, "corpus_robotus")
    corpus_robotus.setPosition( (-0.5, corpus_robotus_y/2 + corpus_robotus_groundClearance, 0.0) )
    #corpus_robotus.setPosition( (-0.5, 0.3, 0.0) )
    #corpus_robotus.color = corpus_robotusDefaultColor[0], corpus_robotusDefaultColor[1], corpus_robotusDefaultColor[2]
    try:
        corpus_robotus.color = [float(integral) for integral in config['color']['corpus_robotusColor']]
    except:
        pass
    bodies.append(corpus_robotus)
    geoms.append(geom)

    """Wheel left"""
    wheelLeft, geom = create_cylinder(world, space, wheel_density, wheel_radius, wheel_length, 3, "wheelLeft")
    wheelLeft.setPosition((0, wheel_radius, corpus_robotus_z/2 + wheel_length/2 + 0.1))
    try:
        wheelLeft.color = [float(integral) for integral in config['color']['wheelLeftColor']]
    except:
        pass

    bodies.append(wheelLeft)
    geoms.append(geom)
    jointWheelLeft = ode.Hinge2Joint(world, None)
    jointWheelLeft.attach(wheelLeft, corpus_robotus)
    wheelLeft_x, wheelLeft_y, wheelLeft_z = wheelLeft.getPosition()
    jointWheelLeft.setAnchor( (wheelLeft_x, wheelLeft_y, corpus_robotus_z/2) )
    jointWheelLeft.setAxis1( (0,1,0) )
    jointWheelLeft.setAxis2( (0,0,1) )
    jointWheelLeft.setParam(ode.ParamHiStop, 0)
    jointWheelLeft.setParam(ode.ParamLoStop, 0)
    
    """Wheel right"""
    wheelRight, geom = create_cylinder(world, space, wheel_density, wheel_radius, wheel_length, 3, "wheelRight")
    wheelRight.setPosition((0, wheel_radius, -corpus_robotus_z/2 - wheel_length/2 - 0.1))
    try:
        wheelRight.color = [float(integral) for integral in config['color']['wheelRightColor']]
    except:
        pass
    
    bodies.append(wheelRight)
    geoms.append(geom)
    jointWheelRight = ode.Hinge2Joint(world, None)
    jointWheelRight.attach(wheelRight, corpus_robotus)
    wheelRight_x, wheelRight_y, wheelRight_z = wheelRight.getPosition()
    jointWheelRight.setAnchor( (wheelRight_x, wheelRight_y, -corpus_robotus_z/2) )
    jointWheelRight.setAxis1( (0,1,0) )
    jointWheelRight.setAxis2( (0,0,1) )
    jointWheelRight.setParam(ode.ParamHiStop, 0)
    jointWheelRight.setParam(ode.ParamLoStop, 0)

    """Foot"""
    foot, geom = create_box(world, space, foot_density, foot_x, foot_y, foot_z, "foot")
    foot.setPosition( (-corpus_robotus_x/2 - 0.5 + foot_x/2, foot_y/2 , 0) )#0.05
    try:
        foot.color = [float(integral) for integral in config['color']['footColor']]
    except:
        pass
    bodies.append(foot)
    geoms.append(geom)
    jointFoot = ode.FixedJoint(world, None)
    jointFoot.attach(foot, corpus_robotus)
    jointFoot.setFixed()
    
    """Arm 1"""
    arm1, geom = create_box(world, space, arm1_density, arm1_x, arm1_y, arm1_z, "arm1")
    arm1.setPosition((-corpus_robotus_x/2 - 0.5 + arm1_edgeDistance + arm1_x/2, arm1_y/2 + corpus_robotus_y + corpus_robotus_groundClearance + arm1_hingeHeight, 0))
    theta = math.radians(90)
    ct = cos (theta)
    st = sin (theta)
    try:
        arm1.color = [float(integral) for integral in config['color']['arm1Color']]
    except:
        pass
    bodies.append(arm1)
    geoms.append(geom)
    jointArm1 = ode.HingeJoint(world, None)
    jointArm1.attach(arm1, corpus_robotus)
    jointArm1.setAnchor( (-corpus_robotus_x/2 - 0.5 + arm1_edgeDistance + arm1_x/2, corpus_robotus_y + corpus_robotus_groundClearance + arm1_hingeHeight, 0) )
    jointArm1.setAxis( (0,0,1) )
    jointArm1.setParam(ode.ParamVel, 0)
    jointArm1.setParam(ode.ParamFMax, 2000)

    """Arm """
    arm2Pos_x, arm2Pos_y, arm2Pos_z = arm1.getPosition()

    arm2, geom = create_box(world, space, arm2_density, arm2_x, arm2_y, arm2_z, "arm2")
    arm2.setPosition((-corpus_robotus_x/2 - 0.5 + arm1_edgeDistance + arm1_x/2, arm2Pos_y + arm1_y/2 + arm2_y/2 + arm2_hingeHeight, 0))
    theta = math.radians(90)
    ct = cos (theta)
    st = sin (theta)
    try:
        arm2.color = [float(integral) for integral in config['color']['arm2Color']]
    except:
        pass
    bodies.append(arm2)
    geoms.append(geom)
    jointArm2 = ode.HingeJoint(world, None)
    jointArm2.attach(arm2, arm1)
    jointArm2.setAnchor( (-corpus_robotus_x/2 - 0.5 + arm1_edgeDistance + arm2_x/2, arm2Pos_y + arm1_y/2 + arm2_hingeHeight, 0) )
    jointArm2.setAxis( (0,0,1) )
    jointArm2.setParam(ode.ParamVel, 0)
    jointArm2.setParam(ode.ParamFMax, 2000)

def moveArm(body, joint, dest):
    #global wheelLeft, oldPos, reward
    #R = body.getRotation()
    cur = math.degrees(joint.getAngle())
    if dest < cur:
        #joint.setParam(ode.ParamHiStop, math.radians(cur + 10))
        joint.setParam(ode.ParamLoStop, math.radians(dest))
        joint.setParam(ode.ParamVel, -armSpeed)
        joint.setParam(ode.ParamFMax, armPower)
    if dest > cur:
        #joint.setParam(ode.ParamLoStop, math.radians(cur - 10))
        joint.setParam(ode.ParamHiStop, math.radians(dest))
        joint.setParam(ode.ParamVel, armSpeed)
        joint.setParam(ode.ParamFMax, armPower)

def moveArmRel(move_x, move_y):
    global arm1, arm2, jointArm1, jointArm2, armState
    try:
        moveArm(arm2, jointArm2, Arm2Positions[armState[0] + move_x -1])
        moveArm(arm1, jointArm1, Arm1Positions[armState[1] - move_y -1]) 
        
        armState = armState[0] + move_x, armState[1] - move_y

        return armState[0], armState[1]
    except:
        print "error in moveArmRel"

def getReward():
    global reward, wheelLeft, oldPos, showRewardValues
    x,y,z = wheelLeft.getPosition()
    x = -x
    
    reward = round(x - oldPos, 2)
    if showRewardValues:
        updateRewardValues()
    oldPos = x

    return reward
    
def _drawfunc ():
    # Draw the scene
    if visualize:
        prepare_GL()
        glEnable(GL_LIGHTING)
        for b in bodies:
                draw_body(b)
        draw_street()
        draw_overlay()
        glDisable(GL_LIGHTING)
    else:
        pyui.desktop.getRenderer().clear()
        glClearColor(0.0,0.0,0.0,0)
	
def onClosed(event):
    print "Dialgo Closed. Value is:", dialog.modal
    pyui.core.quit()

def onMouseMove(event):
    global rotX, rotY, mouseOldXPos, mouseOldYPos, leftMouseButtonPressed
    if leftMouseButtonPressed == True:
        rotX -= ((mouseOldYPos - event.pos[1])  * 180.0) / 300.0
        if rotX > 360.0 or rotX < -360.0:
            rotX = 0.0
        
        rotY -= ((mouseOldXPos - event.pos[0]) * 180.0) / 300.0
        if rotY > 360.0 or rotY < -360.0:
            rotY = 0.0

        mouseOldXPos = event.pos[0]
        mouseOldYPos = event.pos[1]

def onLeftMouseButtonDown(event):
    global leftMouseButtonPressed, mouseOldXPos, mouseOldYPos
    leftMouseButtonPressed = True
    mouseOldXPos = event.pos[0]
    mouseOldYPos = event.pos[1]

def onLeftMouseButtonUp(event):
    global leftMouseButtonPressed, mouseOldXPos, mouseOldYPos
    leftMouseButtonPressed = False

def onMouseWheel(event):
    global zoom
    if event.key == 'UP':
        zoom +=0.05
    if event.key == 'DOWN':
        if zoom > 0.1:
            zoom -=0.05

def colorChosen(color):
    global bodyForColorChanging
    bodyForColorChanging.color = color[0]/255.0, color[1]/255.0, color[2]/255.0 

def onPressColor(event):
    global bodyForColorChanging, frameControl
    for b in bodies:
        if event.text == b.name:
            bodyForColorChanging = b
            
            red = int(b.color[0]*255)
            green = int(b.color[1]*255)
            blue = int(b.color[2]*255)

            newColorDialog = pyui.dialogs.ColorDialog(colorChosen, red, green, blue)
            newColorDialog.title = 'Color Dialog - ' + b.name

def onLogoChosen(filePath):
    global logoFilePath, drawLogo
    logoFilePath = filePath
    drawLogo = True
    prepare_ODE()

def onPressLogo(event):
    logo = pyui.dialogs.FileDialog(os.getcwd()+"/images", onLogoChosen, "")

def onQuit(event):
    global running
    running = False

def onInfo(event):
    global resolutionWidth, resolutionHeight
    width = 200
    height = 100
    frameInfo = pyui.widgets.Frame((resolutionWidth - width)/2, (resolutionHeight - height)/2, width, height, "About")
    frameInfo.setLayout(pyui.layouts.GridLayoutManager(1,5))
    frameInfo.addChild(pyui.widgets.Label("Crawling Robot Simulation"))
    frameInfo.addChild(pyui.widgets.Label("Author:          Markus Schips"))
    frameInfo.addChild(pyui.widgets.Label("Docent:         Prof. Dr. Ertel"))
    frameInfo.addChild(pyui.widgets.Label("Year:            2010"))
    frameInfo.addChild(pyui.widgets.Label("PROJECT WORK"))
    frameInfo.pack()

def onShortcuts(event):
    width = 300
    height = 250
    frameShortcuts = pyui.widgets.Frame((resolutionWidth - width)/2, (resolutionHeight - height)/2, width, height, "Shortcuts")
    frameShortcuts.setLayout(pyui.layouts.GridLayoutManager(1,13))
    frameShortcuts.addChild(pyui.widgets.Label("Crawling Robot Simulation - Shortcuts"))
    frameShortcuts.addChild(pyui.widgets.Label("< s >:               start / stop simulation"))
    frameShortcuts.addChild(pyui.widgets.Label("< space >:        hold / resume simulation"))
    frameShortcuts.addChild(pyui.widgets.Label("< c >:               show / hide <control panel>"))
    frameShortcuts.addChild(pyui.widgets.Label("< r >:                show / hide <robot dimensions>"))
    frameShortcuts.addChild(pyui.widgets.Label("< a >:               show / hide <arm positions>"))
    frameShortcuts.addChild(pyui.widgets.Label("< o >:               show / hide <obstacle settings>"))
    frameShortcuts.addChild(pyui.widgets.Label("< 1-5 >:           move <arm1> to position <1-5>"))
    frameShortcuts.addChild(pyui.widgets.Label("< 6-0 >:           move <arm2> to position <1-5>"))
    frameShortcuts.addChild(pyui.widgets.Label("< , >:               remove gravitation"))
    frameShortcuts.addChild(pyui.widgets.Label("< . >:               set gravitation to <-9.81>"))
    frameShortcuts.addChild(pyui.widgets.Label("< q >:               <quit> application (save config)"))
    frameShortcuts.addChild(pyui.widgets.Label("< Esc >:           kill application"))
    frameShortcuts.pack()

def onKeyDown(event):
    global arm1, arm2, jointArm1, jointArm2, centerX, centerY, running, world, armState, go
    global frameControlPanel, frameRobotPanel, frameArmPanel, frameObstacle
    #print armState
    x_state,y_state = armState
    #print"%s" %\
        #(event.key)
    c = event.key
    if c == 115:#(s)
        if go:
            stop()
        else:
            start()
    if c == 32:#(space)
        hold()
    if c == 99:#(c)
        try:
            if frameControlPanel.id == 0:
                createControls()
            else:
                frameControlPanel._pyuiCloseButton()
        except:
            createControls() 
    if c == 114:#(r)
        try:
            if frameRobotPanel.id == 0:
                createRobotSettings()
            else:
                frameRobotPanel._pyuiCloseButton()
        except:
            createRobotSettings()            
    if c == 97:#(a)
        try:
            if frameArmPanel.id == 0:
                createArmSettings()
            else:
                frameArmPanel._pyuiCloseButton()
        except:
            createArmSettings()
    if c == 111:#(o)
        try:
            if frameObstacle.id == 0:
                createObstacleSettings()
            else:
                frameObstacle._pyuiCloseButton()
        except:
            createObstacleSettings()
    if c == 27:#(Esc)
        sys.exit (0)
    if c == 113:#(Q)
        running = False
    if c == 44:#(,)
         world.setGravity( (0,0,0) )
    if c == 46:#(.)
         world.setGravity( (0,-9.81,0) )
    if c == 49:#(1)
        moveArm(arm1, jointArm1, Arm1Positions[0])
        armState = 1, y_state
    if c == 50:#(2)
        moveArm(arm1, jointArm1, Arm1Positions[1])
        armState = 2, y_state
    if c == 51:#(3)
        moveArm(arm1, jointArm1, Arm1Positions[2])
        armState = 3, y_state
    if c == 52:#(4)
        moveArm(arm1, jointArm1, Arm1Positions[3])
        armState = 4, y_state
    if c == 53:#(5)
        moveArm(arm1, jointArm1, Arm1Positions[4])
        armState = 5, y_state
    if c == 54:#(6)
        moveArm(arm2, jointArm2, Arm2Positions[0])
        armState = x_state, 1
    if c == 55:#(7)
        moveArm(arm2, jointArm2, Arm2Positions[1])
        armState = x_state, 2
    if c == 56:#(8)
        moveArm(arm2, jointArm2, Arm2Positions[2])
        armState = x_state, 3
    if c == 57:#(9)
        moveArm(arm2, jointArm2, Arm2Positions[3])
        armState = x_state, 4
    if c == 48:#(0)
        moveArm(arm2, jointArm2, Arm2Positions[4])
        armState = x_state, 5
    if c == 273:
        centerY+=0.1
    if c == 274:
        centerY-=0.1
    if c == 275:
        centerX+=0.1
    if c == 276:
        centerX-=0.1

def onModifyFloorAngle(value):
    global block, world, space, floorAngle, lastFloorAngle, floorX, floor, theta, checkBoxFixObstacles

    angle_diff = floorAngle - lastFloorAngle
    lastFloorAngle = floorAngle
    floorAngle = math.radians((value -45))
    
    #for i in range(0, 50):    
        #globals()['var%s' % i] = None
    
    floor.disable()

    theta = floorAngle
    ct = cos (theta)
    st = sin (theta)

    floorX = tan(floorAngle)
    floor = ode.GeomPlane(space, (-floorX,1,0), 0)

    for b in bodies: 
        translation(b)
        
        #TODO
        """
        if b.name == 'arm1' or b.name == 'arm2':
            w,x,y,z = b.getQuaternion()
            bodyAngle = math.atan(2 * (w * z + y * y) / (1 - 2 * (y * y + z * z)));

            theta = bodyAngle + angle_diff
            ct = cos (theta)
            st = sin (theta)
            
            b.setRotation([ct, -st, 0, st, ct, 0, 0, 0, 1])
        else:
            theta = floorAngle
            ct = cos (theta)
            st = sin (theta)
            """
        b.setRotation([ct, -st, 0, st, ct, 0, 0, 0, 1])
        
        try:
            if checkBoxFixObstacles.checkState == 1:
                if b.name == "block":
                    globals()['var%s' % b] = ode.FixedJoint(world, None)
                    globals()['var%s' % b].attach(b, floor.getBody())
                    globals()['var%s' % b].setFixed()
        except:
            pass

def onModifyMuFoot(value):
    global mu_foot
    mu_foot = value

def onModifySimulationSpeed(value):
    global simulationSpeed
    simulationSpeed = value

def onModifyMuArm(value):
    global mu_arm
    mu_arm = value

def onModifyMuFloor(value):
    global mu_floor
    mu_floor = value

def onCheckVisualize(value):
    global visualize
    visualize = value

def onCheckAutoCam(value):
    global autoCameraMovement
    autoCameraMovement = value

def onCheckRewardValues(value):
    global showRewardValues
    showRewardValues = value

def onModifyDamping(value):
    global damping
    damping = value/5000.0

def onModifySpring(value):
    global spring
    spring = value * 0.08 + 0.1

def onModifyArmSpeed(value):
    global armSpeed
    armSpeed = value

def onModifyArmPower(value):
    global armPower
    armPower = value

def onPressShowControls(value):
    global frameControlPanel
    try:
        if frameControlPanel.id == 0:
            createControls()
    except:
        createControls()

def createControls():
    global frameControlPanel, labelRobPos, checkBoxAutoCam, autoCameraMovement, showRewardValues

    frameControlPanel = pyui.widgets.Frame(0, 20, 300, 400, "")
    frameControlPanel.setLayout(pyui.layouts.BorderLayoutManager())
    frameControlPanel.title = "Control Panel"
    
    panelControls = pyui.widgets.Panel()
    panelControls.setLayout(pyui.layouts.GridLayoutManager(1,22))
    
    labelFloorAngle = pyui.widgets.Label("Floor Angle")
    panelControls.addChild(labelFloorAngle)
    sliderFloorAngle = pyui.widgets.SliderBar(onModifyFloorAngle, 90, 45)
    sliderFloorAngle.setValue(floorAngle + 45)
    panelControls.addChild(sliderFloorAngle)

    labelSimulationSpeed = pyui.widgets.Label("Simulation Speed")
    panelControls.addChild(labelSimulationSpeed)
    sliderSimulationSpeed = pyui.widgets.SliderBar(onModifySimulationSpeed, 5, 1)
    sliderSimulationSpeed.setValue(simulationSpeed)
    panelControls.addChild(sliderSimulationSpeed)

    labelMuArm = pyui.widgets.Label("Friction Arm")
    panelControls.addChild(labelMuArm)
    sliderMuArm = pyui.widgets.SliderBar(onModifyMuArm, 10000, 1000)
    sliderMuArm.setValue(mu_arm)
    panelControls.addChild(sliderMuArm)

    labelMuFoot = pyui.widgets.Label("Friction Foot")
    panelControls.addChild(labelMuFoot)
    sliderMuFoot = pyui.widgets.SliderBar(onModifyMuFoot, 10000, 1000)
    sliderMuFoot.setValue(mu_foot)
    panelControls.addChild(sliderMuFoot)

    labelMuFloor = pyui.widgets.Label("Friction Floor")
    panelControls.addChild(labelMuFloor)
    sliderMuFloor = pyui.widgets.SliderBar(onModifyMuFloor, 10000, 2000)
    sliderMuFloor.setValue(mu_floor)
    panelControls.addChild(sliderMuFloor)

    checkBoxVisualize = pyui.widgets.CheckBox("Visualize", onCheckVisualize)
    checkBoxVisualize.setCheck(visualize)
    panelControls.addChild(checkBoxVisualize)

    checkBoxAutoCam = pyui.widgets.CheckBox("Auto Cam", onCheckAutoCam)
    checkBoxAutoCam.setCheck(autoCameraMovement)
    panelControls.addChild(checkBoxAutoCam)

    checkBoxRewardValues = pyui.widgets.CheckBox("Reward Values", onCheckRewardValues)
    checkBoxRewardValues.setCheck(showRewardValues)
    panelControls.addChild(checkBoxRewardValues)

    labelDamping = pyui.widgets.Label("Damping")
    panelControls.addChild(labelDamping)
    sliderDamping = pyui.widgets.SliderBar(onModifyDamping, 10, 0)
    sliderDamping.setValue(damping*5000)
    panelControls.addChild(sliderDamping)

    labelSpring= pyui.widgets.Label("Spring")
    panelControls.addChild(labelSpring)
    sliderSpring = pyui.widgets.SliderBar(onModifySpring, 10, 0)
    sliderSpring.setValue((spring - 0.1) /0.08)
    panelControls.addChild(sliderSpring)

    labelArmSpeed= pyui.widgets.Label("Arm Speed")
    panelControls.addChild(labelArmSpeed)
    sliderArmSpeed = pyui.widgets.SliderBar(onModifyArmSpeed, 10, 0)
    sliderArmSpeed.setValue(armSpeed)
    panelControls.addChild(sliderArmSpeed)

    labelArmPower= pyui.widgets.Label("Arm Power")
    panelControls.addChild(labelArmPower)
    sliderArmPower = pyui.widgets.SliderBar(onModifyArmPower, 10000, 0)
    sliderArmPower.setValue(armPower)
    panelControls.addChild(sliderArmPower)

    labelRobPos = pyui.widgets.Label("Robot Position:")
    panelControls.addChild(labelRobPos)


    panelButtons = pyui.widgets.Panel()
    panelButtons.setLayout(pyui.layouts.BorderLayoutManager())

    buttonStart = pyui.widgets.Button("Start", onPressStart)
    panelButtons.addChild(buttonStart, pyui.locals.CENTER)

    buttonHold = pyui.widgets.Button("Hold", onPressHold)
    panelButtons.addChild(buttonHold, pyui.locals.EAST)
        
    buttonStop = pyui.widgets.Button("Stop", onPressStop)
    panelButtons.addChild(buttonStop, pyui.locals.WEST)
    
    frameControlPanel.addChild(panelControls, pyui.locals.CENTER)
    frameControlPanel.addChild(panelButtons, pyui.locals.SOUTH)
            
    frameControlPanel.pack()

def onSetCorpus_RobotusDimensions(event):
    global corpus_robotus_x, corpus_robotus_y, corpus_robotus_z, corpus_robotus_density, corpus_robotus_groundClearance
    global numberEditCorpus_Robotus_x, numberEditCorpus_Robotus_y, numberEditCorpus_Robotus_z, numberEditCorpus_Robotus_density, numberEditCorpus_Robotus_groundClearance

    corpus_robotus_x = float(numberEditCorpus_Robotus_x.text)
    corpus_robotus_y = float(numberEditCorpus_Robotus_y.text)
    corpus_robotus_z = float(numberEditCorpus_Robotus_z.text)
    corpus_robotus_density = float(numberEditCorpus_Robotus_density.text)
    corpus_robotus_groundClearance = float(numberEditCorpus_Robotus_groundClearance.text)
    prepare_ODE()

def onResetCorpus_RobotusDimensions(event):
    global corpus_robotus_x, corpus_robotus_y, corpus_robotus_z, corpus_robotus_density, corpus_robotus_groundClearance
    global numberEditCorpus_Robotus_x, numberEditCorpus_Robotus_y, numberEditCorpus_Robotus_z, numberEditCorpus_Robotus_density, numberEditCorpus_Robotus_groundClearance
    corpus_robotus_x = corpus_robotus_xDefault
    corpus_robotus_y = corpus_robotus_yDefault
    corpus_robotus_z = corpus_robotus_zDefault
    corpus_robotus_density = corpus_robotus_densityDefault
    corpus_robotus_groundClearance = corpus_robotus_groundClearanceDefault

    numberEditCorpus_Robotus_x.setText(str(corpus_robotus_xDefault))
    numberEditCorpus_Robotus_y.setText(str(corpus_robotus_yDefault))
    numberEditCorpus_Robotus_z.setText(str(corpus_robotus_zDefault))
    numberEditCorpus_Robotus_density.setText(str(corpus_robotus_densityDefault))
    numberEditCorpus_Robotus_groundClearance.setText(str(corpus_robotus_groundClearanceDefault))
    prepare_ODE()    

def onSetFootDimensions(event):
    global foot_x, foot_y, foot_z, foot_density
    global numberEditFoot_x, numberEditFoot_y, numberEditFoot_z, numberEditFoot_density

    foot_x = float(numberEditFoot_x.text)
    foot_y = float(numberEditFoot_y.text)
    foot_z = float(numberEditFoot_z.text)
    foot_density = float(numberEditFoot_density.text)
    prepare_ODE()

def onResetFootDimensions(event):
    global foot_x, foot_y, foot_z, foot_density
    global numberEditFoot_x, numberEditFoot_y, numberEditFoot_z, numberEditFoot_density
    foot_x = foot_xDefault
    foot_y = foot_yDefault
    foot_z = foot_zDefault
    foot_density = foot_densityDefault

    numberEditFoot_x.setText(str(foot_xDefault))
    numberEditFoot_y.setText(str(foot_yDefault))
    numberEditFoot_z.setText(str(foot_zDefault))
    numberEditFoot_density.setText(str(foot_densityDefault))
    
    prepare_ODE()

def onSetWheelDimensions(event):
    global wheel_radius, wheel_length, wheel_density
    global numberEditWheel_radius, numberEditWheel_length, numberEditWheel_density

    wheel_radius = float(numberEditWheel_radius.text)
    wheel_length = float(numberEditWheel_length.text)
    wheel_density = float(numberEditWheel_density.text)
    prepare_ODE()

def onResetWheelDimensions(event):
    global wheel_radius, wheel_length, wheel_density
    global numberEditWheel_radius, numberEditWheel_length, numberEditWheel_density
    wheel_radius = wheel_radiusDefault
    wheel_length = wheel_lengthDefault
    wheel_density = wheel_densityDefault

    numberEditWheel_radius.setText(str(wheel_radiusDefault))
    numberEditWheel_length.setText(str(wheel_lengthDefault))
    numberEditWheel_density.setText(str(wheel_densityDefault))
    
    prepare_ODE()

def onSetArm1Dimensions(event):
    global arm1_x, arm1_y, arm1_z, arm1_density, arm1_hingeHeight, arm1_edgeDistance
    global numberEditArm1_x, numberEditArm1_y, numberEditArm1_z, numberEditArm1_density, numberEditArm1_hingeHeight, numberEditArm1_edgeDistance

    arm1_x = float(numberEditArm1_x.text)
    arm1_y = float(numberEditArm1_y.text)
    arm1_z = float(numberEditArm1_z.text)
    arm1_density = float(numberEditArm1_density.text)
    arm1_hingeHeight = float(numberEditArm1_hingeHeight.text)
    arm1_edgeDistance = float(numberEditArm1_edgeDistance.text)
    prepare_ODE()

def onResetArm1Dimensions(event):
    global arm1_x, arm1_y, arm1_z, arm1_density, arm1_hingeHeight, arm1_edgeDistance
    global numberEditArm1_x, numberEditArm1_y, numberEditArm1_z, numberEditArm1_density, numberEditArm1_hingeHeight, numberEditArm1_edgeDistance
    arm1_x = arm1_xDefault
    arm1_y = arm1_yDefault
    arm1_z = arm1_zDefault
    arm1_density = arm1_densityDefault
    arm1_edgeDistance = arm1_edgeDistanceDefault
    arm1_hingeHeight = arm1_hingeHeightDefault

    numberEditArm1_x.setText(str(arm1_xDefault))
    numberEditArm1_y.setText(str(arm1_yDefault))
    numberEditArm1_z.setText(str(arm1_zDefault))
    numberEditArm1_density.setText(str(arm1_densityDefault))
    numberEditArm1_hingeHeight.setText(str(arm1_hingeHeightDefault))
    numberEditArm1_edgeDistance.setText(str(arm1_edgeDistanceDefault))
    
    prepare_ODE()

def onSetArm2Dimensions(event):
    global arm2_x, arm2_y, arm2_z, arm2_density, arm2_hingeHeight
    global numberEditArm2_x, numberEditArm2_y, numberEditArm2_z, numberEditArm2_density, numberEditArm2_hingeHeight

    arm2_x = float(numberEditArm2_x.text)
    arm2_y = float(numberEditArm2_y.text)
    arm2_z = float(numberEditArm2_z.text)
    arm2_density = float(numberEditArm2_density.text)
    arm2_hingeHeight = float(numberEditArm2_hingeHeight.text)
    prepare_ODE()

def onResetArm2Dimensions(event):
    global arm2_x, arm2_y, arm2_z, arm2_density, arm2_hingeHeight
    global numberEditArm2_x, numberEditArm2_y, numberEditArm2_z, numberEditArm2_density, numberEditArm1_hingeHeight
    
    arm2_x = arm2_xDefault
    arm2_y = arm2_yDefault
    arm2_z = arm2_zDefault
    arm2_density = arm2_densityDefault
    arm2_hingeHeight = arm2_hingeHeightDefault

    numberEditArm2_x.setText(str(arm2_xDefault))
    numberEditArm2_y.setText(str(arm2_yDefault))
    numberEditArm2_z.setText(str(arm2_zDefault))
    numberEditArm2_density.setText(str(arm2_densityDefault))
    numberEditArm1_hingeHeight.setText(str(arm2_hingeHeightDefault))
    
    prepare_ODE()

def onPressShowRobotSettings(value):
    global frameRobotPanel
    try:
        if frameRobotPanel.id == 0:
            createRobotSettings()
    except:
        createRobotSettings()
    
def createRobotSettings():
    global frameRobotPanel
    global numberEditCorpus_Robotus_x, numberEditCorpus_Robotus_y, numberEditCorpus_Robotus_z, numberEditCorpus_Robotus_density, numberEditCorpus_Robotus_groundClearance
    global numberEditFoot_x, numberEditFoot_y, numberEditFoot_z, numberEditFoot_density
    global numberEditWheel_radius, numberEditWheel_length, numberEditWheel_density
    global numberEditArm1_x, numberEditArm1_y, numberEditArm1_z, numberEditArm1_density, numberEditArm1_hingeHeight, numberEditArm1_edgeDistance
    global numberEditArm2_x, numberEditArm2_y, numberEditArm2_z, numberEditArm2_density, numberEditArm2_hingeHeight

    width = 300
    height = 200
    frameRobotPanel = pyui.widgets.Frame(pyui.desktop.getDesktop().width - width - 10, 344, width, height, "Robot Dimensions")
    tabbed = pyui.widgets.TabbedPanel()
    for title in ("Corpus Robotus", "Foot", "Wheels", "Arm 1", "Arm 2"):
        tabbed.addPanel(title)

    frameRobotPanel.replacePanel(tabbed)

    ###Corpus Robotus############################################################
    tabbed.getPanel(0).setLayout(pyui.layouts.GridLayoutManager(2,6))

    #Dimension
    tabbed.getPanel(0).addChild(pyui.widgets.Label("Dimension x: "))
    numberEditCorpus_Robotus_x = pyui.widgets.NumberEdit(str(corpus_robotus_x), 20, None, True)
    tabbed.getPanel(0).addChild(numberEditCorpus_Robotus_x)

    tabbed.getPanel(0).addChild(pyui.widgets.Label("Dimension y: "))
    numberEditCorpus_Robotus_y = pyui.widgets.NumberEdit(str(corpus_robotus_y), 20, None, True)
    tabbed.getPanel(0).addChild(numberEditCorpus_Robotus_y)

    tabbed.getPanel(0).addChild(pyui.widgets.Label("Dimension z: "))
    numberEditCorpus_Robotus_z = pyui.widgets.NumberEdit(str(corpus_robotus_z), 20, None, True)
    tabbed.getPanel(0).addChild(numberEditCorpus_Robotus_z)

    #Densitiy
    tabbed.getPanel(0).addChild(pyui.widgets.Label("Density: "))
    numberEditCorpus_Robotus_density = pyui.widgets.NumberEdit(str(corpus_robotus_density), 20, None, True)
    tabbed.getPanel(0).addChild(numberEditCorpus_Robotus_density)

    #Ground Clearance
    tabbed.getPanel(0).addChild(pyui.widgets.Label("Ground Clearance: "))
    numberEditCorpus_Robotus_groundClearance = pyui.widgets.NumberEdit(str(corpus_robotus_groundClearance), 20, None, True)
    tabbed.getPanel(0).addChild(numberEditCorpus_Robotus_groundClearance)

    tabbed.getPanel(0).addChild(pyui.widgets.Button("Reset", onResetCorpus_RobotusDimensions))
    tabbed.getPanel(0).addChild(pyui.widgets.Button("Set", onSetCorpus_RobotusDimensions))

    ###Foot############################################################
    tabbed.getPanel(1).setLayout(pyui.layouts.GridLayoutManager(2,5))

    #Dimension
    tabbed.getPanel(1).addChild(pyui.widgets.Label("Dimension x: "))
    numberEditFoot_x = pyui.widgets.NumberEdit(str(foot_x), 20, None, True)
    tabbed.getPanel(1).addChild(numberEditFoot_x)

    tabbed.getPanel(1).addChild(pyui.widgets.Label("Dimension y: "))
    numberEditFoot_y = pyui.widgets.NumberEdit(str(foot_y), 20, None, True)
    tabbed.getPanel(1).addChild(numberEditFoot_y)

    tabbed.getPanel(1).addChild(pyui.widgets.Label("Dimension z: "))
    numberEditFoot_z = pyui.widgets.NumberEdit(str(foot_z), 20, None, True)
    tabbed.getPanel(1).addChild(numberEditFoot_z)

    #Density
    tabbed.getPanel(1).addChild(pyui.widgets.Label("Density: "))
    numberEditFoot_density = pyui.widgets.NumberEdit(str(foot_density), 20, None, True)
    tabbed.getPanel(1).addChild(numberEditFoot_density)

    tabbed.getPanel(1).addChild(pyui.widgets.Button("Reset", onResetFootDimensions))
    tabbed.getPanel(1).addChild(pyui.widgets.Button("Set", onSetFootDimensions))

    ###Wheels############################################################
    tabbed.getPanel(2).setLayout(pyui.layouts.GridLayoutManager(2,4))

    #Dimension
    tabbed.getPanel(2).addChild(pyui.widgets.Label("Radius: "))
    numberEditWheel_radius = pyui.widgets.NumberEdit(str(wheel_radius), 20, None, True)
    tabbed.getPanel(2).addChild(numberEditWheel_radius)

    tabbed.getPanel(2).addChild(pyui.widgets.Label("Length: "))
    numberEditWheel_length = pyui.widgets.NumberEdit(str(wheel_length), 20, None, True)
    tabbed.getPanel(2).addChild(numberEditWheel_length)

    #Density
    tabbed.getPanel(2).addChild(pyui.widgets.Label("Density: "))
    numberEditWheel_density = pyui.widgets.NumberEdit(str(wheel_density), 20, None, True)
    tabbed.getPanel(2).addChild(numberEditWheel_density)

    tabbed.getPanel(2).addChild(pyui.widgets.Button("Reset", onResetWheelDimensions))
    tabbed.getPanel(2).addChild(pyui.widgets.Button("Set", onSetWheelDimensions))

    ###Arm 1############################################################
    tabbed.getPanel(3).setLayout(pyui.layouts.GridLayoutManager(2,7))

    #Dimension
    tabbed.getPanel(3).addChild(pyui.widgets.Label("Dimension x: "))
    numberEditArm1_x = pyui.widgets.NumberEdit(str(arm1_x), 20, None, True)
    tabbed.getPanel(3).addChild(numberEditArm1_x)

    tabbed.getPanel(3).addChild(pyui.widgets.Label("Dimension y: "))
    numberEditArm1_y = pyui.widgets.NumberEdit(str(arm1_y), 20, None, True)
    tabbed.getPanel(3).addChild(numberEditArm1_y)

    tabbed.getPanel(3).addChild(pyui.widgets.Label("Dimension z: "))
    numberEditArm1_z = pyui.widgets.NumberEdit(str(arm1_z), 20, None, True)
    tabbed.getPanel(3).addChild(numberEditArm1_z)

    #Density
    tabbed.getPanel(3).addChild(pyui.widgets.Label("Density: "))
    numberEditArm1_density = pyui.widgets.NumberEdit(str(arm1_density), 20, None, True)
    tabbed.getPanel(3).addChild(numberEditArm1_density)

    #Hinge Height
    tabbed.getPanel(3).addChild(pyui.widgets.Label("Hinge Height: "))
    numberEditArm1_hingeHeight = pyui.widgets.NumberEdit(str(arm1_hingeHeight), 20, None, True)
    tabbed.getPanel(3).addChild(numberEditArm1_hingeHeight)

    #Edge Distance
    tabbed.getPanel(3).addChild(pyui.widgets.Label("Edge Distance: "))
    numberEditArm1_edgeDistance = pyui.widgets.NumberEdit(str(arm1_edgeDistance), 20, None, True)
    tabbed.getPanel(3).addChild(numberEditArm1_edgeDistance)

    tabbed.getPanel(3).addChild(pyui.widgets.Button("Reset", onResetArm1Dimensions))
    tabbed.getPanel(3).addChild(pyui.widgets.Button("Set", onSetArm1Dimensions))

    ###Arm 2############################################################
    tabbed.getPanel(4).setLayout(pyui.layouts.GridLayoutManager(2,6))

    #Dimension
    tabbed.getPanel(4).addChild(pyui.widgets.Label("Dimension x: "))
    numberEditArm2_x = pyui.widgets.NumberEdit(str(arm2_x), 20, None, True)
    tabbed.getPanel(4).addChild(numberEditArm2_x)

    tabbed.getPanel(4).addChild(pyui.widgets.Label("Dimension y: "))
    numberEditArm2_y = pyui.widgets.NumberEdit(str(arm2_y), 20, None, True)
    tabbed.getPanel(4).addChild(numberEditArm2_y)

    tabbed.getPanel(4).addChild(pyui.widgets.Label("Dimension z: "))
    numberEditArm2_z = pyui.widgets.NumberEdit(str(arm2_z), 20, None, True)
    tabbed.getPanel(4).addChild(numberEditArm2_z)

    #Density
    tabbed.getPanel(4).addChild(pyui.widgets.Label("Density: "))
    numberEditArm2_density = pyui.widgets.NumberEdit(str(arm2_density), 20, None, True)
    tabbed.getPanel(4).addChild(numberEditArm2_density)

    #Hinge Height
    tabbed.getPanel(4).addChild(pyui.widgets.Label("Hinge Height: "))
    numberEditArm2_hingeHeight = pyui.widgets.NumberEdit(str(arm2_hingeHeight), 20, None, True)
    tabbed.getPanel(4).addChild(numberEditArm2_hingeHeight)

    tabbed.getPanel(4).addChild(pyui.widgets.Button("Reset", onResetArm2Dimensions))
    tabbed.getPanel(4).addChild(pyui.widgets.Button("Set", onSetArm2Dimensions))

    frameRobotPanel.pack()

def onModifyArm1Positions(value):
    global Arm1Positions, tabbed
    i=0
    for p in tabbed.getPanel(0).children:
        Arm1Positions[i] = p.position
        i+=1

def onModifyArm2Positions(value):
    global Arm2Positions, tabbed
    i=0
    for p in tabbed.getPanel(1).children:
        Arm2Positions[i] = p.position
        i+=1

def onPressShowArmSettings(value):
    global frameArmPanel
    try:
        if frameArmPanel.id == 0:
            createArmSettings()
    except:
        createArmSettings()

def createArmSettings():
    global frameArmPanel, Arm1Positions, sliderArm1Position_1, tabbed
    frameArmPanel = pyui.widgets.Frame(0, 444, 300, 200, "Arm Positions")
    tabbed = pyui.widgets.TabbedPanel()
    for title in ("Arm 1", "Arm 2"):
        tabbed.addPanel(title)

    frameArmPanel.replacePanel(tabbed)
    tabbed.getPanel(0).setLayout(pyui.layouts.GridLayoutManager(1,len(Arm1Positions)))

    for i in range(0,len(Arm1Positions)):
        sliderArm1Position = pyui.widgets.SliderBar(onModifyArm1Positions, 90, Arm1Positions[i])
        tabbed.getPanel(0).addChild(sliderArm1Position)

    tabbed.getPanel(1).setLayout(pyui.layouts.GridLayoutManager(1,len(Arm2Positions)))

    for i in range(0,len(Arm2Positions)):
        sliderArm2Position = pyui.widgets.SliderBar(onModifyArm2Positions, 90, Arm2Positions[i])
        tabbed.getPanel(1).addChild(sliderArm2Position)
  
    frameArmPanel.pack()

def onPressStart(event):
    start()

def start():
    global go
    go = True
    
def onPressStop(event):
    stop()

def onPressHold(event):
    hold()

def hold():
    global go
    if go:
        go = False
    else:
        go = True

def stop():
    global go, floorAngle, frameControlPanel, floor, centerX, centerY, centerZ, checkBoxAutoCam, reward, armState
    prepare_ODE()
    floor.disable()
    floorAngle = 0
    floorX = tan(floorAngle)
    floor = ode.GeomPlane(space, (-floorX,1,0), 0)
    removeObstacles()
    #checkBoxAutoCam.setCheck(0)
    centerX, centerY, centerZ = corpus_robotus.getPosition()
    go = False
    frameControlPanel.destroy()
    createControls()
    rewardValues.clear()
    reward = 0.0
    armState = initialArmState

    return 0

def onResolution(event):
    global resolutionWidth, resolutionHeight
    posOfX = event.text.find('x')
    resolutionWidth = int(event.text[0:posOfX-1])
    resolutionHeight = int(event.text[posOfX+2:len(event.text)])
    dialog = pyui.dialogs.StdDialog("Resolution changing", "Resolution changes next time starting the application", 1)# + event.text, 1)

    dialog.doModal()

def onPressResetAll(event):
    pyui.quit()
    os.remove('conf')

def onCheckObstacleDirection(event):
    pass

def onShowObstacleDialog(event):
    global frameObstacle
    try:
        if frameObstacle.id == 0:
            createObstacleSettings()
    except:
        createObstacleSettings()

def createObstacleSettings():
    global frameObstacle, dropDownBoxObstacleType, numberEditAmountOfObstacles, numberEditDistanceOfObstacles, numberEditDimension_xOfObstacles, numberEditDimension_yOfObstacles, numberEditDimension_zOfObstacles, numberEditDensityOfObstacles, checkBoxFixObstacles, numberEditStartingDistance
    global amountOfObstacles, distanceOfObstacles, dimension_xOfObstacles, dimension_yOfObstacles, dimension_zOfObstacles, densityOfObstacles, fixObstacles, startingDistance
    
    width = 200
    height = 300
    frameObstacle = pyui.widgets.Frame(pyui.desktop.getDesktop().width - width - 10, 20, width, height, "Obstacle Settings")
    frameObstacle.setLayout(pyui.layouts.BorderLayoutManager())

    panelContainer = pyui.widgets.Panel()
    panelContainer.setLayout(pyui.layouts.BorderLayoutManager())
    #CENTER
    panelObstacleSettings = pyui.widgets.Panel()
    #Type
    panelObstacleSettings.addChild(pyui.widgets.Label("Obstacle Type: "))
    panelObstacleSettings.setLayout(pyui.layouts.GridLayoutManager(2,9))
    dropDownBoxObstacleType = pyui.widgets.DropDownBox(4)
    dropDownBoxObstacleType.addItem("box", None)
    panelObstacleSettings.addChild(dropDownBoxObstacleType)
    #Number
    panelObstacleSettings.addChild(pyui.widgets.Label("Amount: "))
    numberEditAmountOfObstacles = pyui.widgets.NumberEdit(str(amountOfObstacles), 20, None, True)
    panelObstacleSettings.addChild(numberEditAmountOfObstacles)
    #Distance
    panelObstacleSettings.addChild(pyui.widgets.Label("Distance: "))
    numberEditDistanceOfObstacles = pyui.widgets.NumberEdit(str(distanceOfObstacles), 20, None, True)
    panelObstacleSettings.addChild(numberEditDistanceOfObstacles)
    #Dimension
    panelObstacleSettings.addChild(pyui.widgets.Label("Dimension x: "))
    numberEditDimension_xOfObstacles = pyui.widgets.NumberEdit(str(dimension_xOfObstacles), 20, None, True)
    panelObstacleSettings.addChild(numberEditDimension_xOfObstacles)

    panelObstacleSettings.addChild(pyui.widgets.Label("Dimension y: "))
    numberEditDimension_yOfObstacles = pyui.widgets.NumberEdit(str(dimension_yOfObstacles), 20, None, True)
    panelObstacleSettings.addChild(numberEditDimension_yOfObstacles)

    panelObstacleSettings.addChild(pyui.widgets.Label("Dimension z: "))
    numberEditDimension_zOfObstacles = pyui.widgets.NumberEdit(str(dimension_zOfObstacles), 20, None, True)
    panelObstacleSettings.addChild(numberEditDimension_zOfObstacles)

    #Density
    panelObstacleSettings.addChild(pyui.widgets.Label("Density: "))
    numberEditDensityOfObstacles = pyui.widgets.NumberEdit(str(densityOfObstacles), 20, None, True)
    panelObstacleSettings.addChild(numberEditDensityOfObstacles)

    #Fix
    panelObstacleSettings.addChild(pyui.widgets.Label("Fix Obstacles: "))
    checkBoxFixObstacles = pyui.widgets.CheckBox("", None)
    checkBoxFixObstacles.setCheck(fixObstacles)
    panelObstacleSettings.addChild(checkBoxFixObstacles)

    #Starting Distance
    panelObstacleSettings.addChild(pyui.widgets.Label("Starting Distance: "))
    numberEditStartingDistance = pyui.widgets.NumberEdit(str(startingDistance), 20, None, True)
    panelObstacleSettings.addChild(numberEditStartingDistance)

    panelContainer.addChild(panelObstacleSettings, pyui.locals.CENTER)
    
    frameObstacle.addChild(panelContainer, pyui.locals.CENTER) 

    #SOUTH
    panelSouth = pyui.widgets.Panel()
    panelSouth.setLayout(pyui.layouts.GridLayoutManager(2,1))
    
    buttonAddObstacles = pyui.widgets.Button("add", onPressAddObstacles)
    panelSouth.addChild(buttonAddObstacles)

    buttonRemoveObstacles = pyui.widgets.Button("remove", onRemoveObstacles)
    panelSouth.addChild(buttonRemoveObstacles)

    frameObstacle.addChild(panelSouth, pyui.locals.SOUTH) 

    frameObstacle.pack()

semaphoreObstacle = True
    
def onPressAddObstacles(value):
    global semaphoreObstacle, block, floor, floorAngle, world, space, bodies, frameObstacle, dropDownBoxObstacleType, numberEditAmountOfObstacles, numberEditDistanceOfObstacles, numberEditDimension_xOfObstacles, numberEditDimension_yOfObstacles, numberEditDimension_zOfObstacles, numberEditDensityOfObstacles, checkBoxFixObstacles, numberEditStartingDistance
    global amountOfObstacles, distanceOfObstacles, dimension_xOfObstacles, dimension_yOfObstacles, dimension_zOfObstacles, densityOfObstacles, fixObstacles, startingDistance
    
    if semaphoreObstacle == True:
        dimension_xOfObstacles = float(numberEditDimension_xOfObstacles.text)
        dimension_yOfObstacles = float(numberEditDimension_yOfObstacles.text)
        dimension_zOfObstacles = float(numberEditDimension_zOfObstacles.text)

        amountOfObstacles = int(numberEditAmountOfObstacles.text)
        distanceOfObstacles = float(numberEditDistanceOfObstacles.text)
        densityOfObstacles = float(numberEditDensityOfObstacles.text)
        fixObstacles = checkBoxFixObstacles.checkState
        startingDistance = float(numberEditStartingDistance.text)
        
        for i in range(0, amountOfObstacles):
        
            block, geom = create_box(world, space, densityOfObstacles, dimension_xOfObstacles, dimension_yOfObstacles, dimension_zOfObstacles, "block")
         
            xt = -distanceOfObstacles*i-startingDistance
            yt = dimension_yOfObstacles/2
            zt = 0
            
            if xt == 0:
                object_angle = math.pi/2
            else:
                object_angle = (atan(yt/xt))

            radius_body = sqrt(pow(yt,2) + pow(xt,2))
            if xt < 0:
                radius_body = -radius_body

            xBody = radius_body * cos(floorAngle + object_angle)
            yBody = radius_body * sin(floorAngle + object_angle)

            block.setPosition((xBody,yBody,zt))

            theta = floorAngle
            ct = cos (theta)
            st = sin (theta)

            block.setRotation([ct, -st, 0, st, ct, 0, 0, 0, 1])
            
            bodies.append(block)
            geoms.append(geom)
            
            if fixObstacles == 1:
                globals()['var%s' % i] = ode.FixedJoint(world, None)
                globals()['var%s' % i].attach(block, floor.getBody())
                globals()['var%s' % i].setFixed()

        semaphoreObstacle = False
    else:
        dialog = pyui.dialogs.StdDialog("Warning", "please first remove the old obstacles", 1)
        dialog.doModal()

def onRemoveObstacles(value):
    removeObstacles()

def removeObstacles():
    global bodies, space, semaphoreObstacle
    #print "remove obstacles"

    allKilled = False

    while allKilled != True:

        allKilled = True
        for i in range(0, space.getNumGeoms()):
            try:
                if space.getGeom(i).getBody().name == 'block':
                    allKilled = False
                    bodies.remove(space.getGeom(i).getBody())
                    space.remove(space.getGeom(i))
            except:
                pass
    semaphoreObstacle = True

def createMenu():
    global frameControlPanel, bodies

    menuBar = pyui.widgets.MenuBar()

    menuControls = pyui.widgets.Menu("Controls")
    menuControls.addItem("Show Controls", onPressShowControls)
    menuBar.addMenu(menuControls)

    robotControls = pyui.widgets.Menu("Robot")
    robotControls.addItem("Robot Settings", onPressShowRobotSettings)
    menuBar.addMenu(robotControls)

    armControls = pyui.widgets.Menu("Arm")
    armControls.addItem("Arm Settings", onPressShowArmSettings)
    menuBar.addMenu(armControls)

    menuObstacles = pyui.widgets.Menu("Obstacle")
    menuObstacles.addItem("add obstacles", onShowObstacleDialog)
    menuBar.addMenu(menuObstacles)

    menuColor = pyui.widgets.Menu("Color")
    for b in bodies:
        menuColor.addItem(b.name, onPressColor)
    menuColor.addItem("Logo", onPressLogo)
    
    menuBar.addMenu(menuColor)

    resolutionMenu = pyui.widgets.Menu("Resolution")

    resolutionMenu.addItem("800 x 600", onResolution)
    resolutionMenu.addItem("1024 x 800", onResolution)
    resolutionMenu.addItem("1280 x 800", onResolution)
    resolutionMenu.addItem("1400 x 900", onResolution)
    resolutionMenu.addItem("1600 x 900", onResolution)
    resolutionMenu.addItem("1600 x 1024", onResolution)
    resolutionMenu.addItem("1920 x 1080", onResolution)

    menuBar.addMenu(resolutionMenu)

    menuResetAll = pyui.widgets.Menu("Reset All")
    menuResetAll.addItem("Do it", onPressResetAll)
    menuBar.addMenu(menuResetAll)

    menuInfo = pyui.widgets.Menu("Info")
    menuInfo.addItem("About", onInfo)
    menuBar.addMenu(menuInfo)

    menuHelp = pyui.widgets.Menu("Help")
    menuHelp.addItem("Shortcuts", onShortcuts)
    menuBar.addMenu(menuHelp)

    exitMenu = pyui.widgets.Menu("Exit")
    exitMenu.addItem("Bye", onQuit)
    menuBar.addMenu(exitMenu)

def _idlefunc():
    global counter, state, lasttime, mu, simulationSpeed, mu_foot, mu_arm, mu_wheels, corpus_robotus, centerX, centerY, centerZ, autoCameraMovement, world, contactgroup, space, dt, labelRobPos, lastPos

    t = dt - (time.time() - lasttime)
    
    if t > 0:
        time.sleep(t)

    x,y,z = wheelLeft.getPosition()

    labelRobPos.setText("Robot Position: " + str(round(-x, 2)))
        
    # Simulate
    n = 4

    for i in range(n):
        # Detect collisions and create contact joints
        space.collide((world,contactgroup), collision_callback)

        # Simulation step
        if simulationSpeed == 0:
            simulationSpeed = 0.5
        world.step(dt/n*simulationSpeed)

        # Remove all contact joints
        contactgroup.empty()
        
        #Auto Camera Movement
        if autoCameraMovement == 1:
            centerX, centerY, centerZ = corpus_robotus.getPosition()

    lasttime = time.time()

from pyui.themes import comic, future, green, win2k

def saveConfiguration():
    print "saving configuration..."
    config = ConfigObj()
    config.filename = "conf"
    
    config['color'] = {}
    config['color']['corpus_robotusColor'] = corpus_robotus.color
    config['color']['wheelLeftColor'] = wheelLeft.color
    config['color']['wheelRightColor'] = wheelRight.color
    config['color']['arm1Color'] = arm1.color
    config['color']['arm2Color'] = arm2.color
    config['color']['footColor'] = foot.color

    config['logo'] = {}
    config['logo']['logoFilePath'] = logoFilePath
    
    config['settings'] = {}
    config['settings']['resolutionWidth'] = resolutionWidth
    config['settings']['resolutionHeight'] = resolutionHeight
    config['settings']['visualize'] = visualize
    config['settings']['autoCameraMovement'] = autoCameraMovement
    config['settings']['showRewardValues'] = showRewardValues

    config['settings']['damping'] = damping
    config['settings']['spring'] = spring
    config['settings']['armSpeed'] = armSpeed
    config['settings']['armPower'] = armPower
    config['settings']['simulationSpeed'] = simulationSpeed
    config['settings']['mu_foot'] = mu_foot
    config['settings']['mu_arm'] = mu_arm
    config['settings']['mu_wheels'] = mu_wheels
    config['settings']['mu_floor'] = mu_floor
    
    config['settings']['arm1Positions'] = Arm1Positions
    config['settings']['arm2Positions'] = Arm2Positions

    config['robot'] = {}
    config['robot']['corpus_robotus_x'] = corpus_robotus_x
    config['robot']['corpus_robotus_y'] = corpus_robotus_y
    config['robot']['corpus_robotus_z'] = corpus_robotus_z
    config['robot']['corpus_robotus_density'] = corpus_robotus_density
    config['robot']['corpus_robotus_groundClearance'] = corpus_robotus_groundClearance
    
    config['robot']['wheel_radius'] = wheel_radius
    config['robot']['wheel_length'] = wheel_length
    config['robot']['wheel_density'] = wheel_density

    config['robot']['foot_x'] = foot_x
    config['robot']['foot_y'] = foot_y
    config['robot']['foot_z'] = foot_z
    config['robot']['foot_density'] = foot_density

    config['robot']['arm1_x'] = arm1_x
    config['robot']['arm1_y'] = arm1_y
    config['robot']['arm1_z'] = arm1_z
    config['robot']['arm1_density'] = arm1_density
    config['robot']['arm1_edgeDistance'] = arm1_edgeDistance
    config['robot']['arm1_hingeHeight'] = arm1_hingeHeight

    config['robot']['arm2_x'] = arm2_x
    config['robot']['arm2_y'] = arm2_y
    config['robot']['arm2_z'] = arm2_z
    config['robot']['arm2_density'] = arm2_density
    config['robot']['arm2_hingeHeight'] = arm2_hingeHeight

    config['obstacles'] = {}
    config['obstacles']['amount'] = amountOfObstacles
    config['obstacles']['distance'] = distanceOfObstacles
    config['obstacles']['dimensionX'] = dimension_xOfObstacles
    config['obstacles']['dimensionY'] = dimension_yOfObstacles
    config['obstacles']['dimensionZ'] = dimension_zOfObstacles
    config['obstacles']['density'] = densityOfObstacles
    config['obstacles']['fixObstacles'] = fixObstacles
    config['obstacles']['startingDistance'] = startingDistance

    config.write()
    print "config saved"
    
desktop = None


def updateRewardValues():
    global reward, maxReward, rewardValues

    if reward > maxReward:
        maxReward = reward
            
    rewardValues.append(reward)

def run():
    global desktop, resolutionWidth, resolutionHeight, centerX, centerY, centerZ
    global go,rotX, rotY, rotZ, zoom, armState

    ################################## SimpleXMLRPCServer
    import xmlrpclib
    from SimpleXMLRPCServer import SimpleXMLRPCServer
    portnumber = 7777
    server = SimpleXMLRPCServer( ("", portnumber), logRequests=False, allow_none=True)
    ip = socket.gethostbyname(socket.gethostname())
    print 'Server: %(ip)s listening on port: %(portnumber)d' % vars()

    server.register_function(start, "start")
    server.register_function(stop, "stop")
    server.register_function(moveArmRel, "moveArmRel")
    server.register_function(getReward, "getReward")
    server.timeout = 0.01
    ################################## SimpleXMLRPCServer 
       
    pygame.init()
    os.environ["SDL_VIDEO_CENTERED"] = "1" # center pygame window on screen

    pyui.init(resolutionWidth-12, resolutionHeight-40,"p3d", 0, "Crawling Robot Simulation")

    pygame.display.set_icon(pygame.image.load('images/WalkingRobotSimulation.png'))
    prepare_ODE()

    centerX, centerY, centerZ = corpus_robotus.getPosition()

    pyui.desktop.getRenderer().setBackMethod(_drawfunc)
    
    pyui.desktop.getDesktop().registerHandler(pyui.locals.MOUSEMOVE, onMouseMove)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.LMOUSEBUTTONDOWN, onLeftMouseButtonDown)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.LMOUSEBUTTONUP, onLeftMouseButtonUp)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.MMOUSEBUTTONDOWN, onMouseWheel)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.MMOUSEBUTTONUP, onMouseWheel)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.MOUSEWHEEL, onMouseWheel)
    pyui.desktop.getDesktop().registerHandler(pygame.MOUSEBUTTONDOWN, onMouseWheel)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.SCROLLPOS, onMouseWheel)
    pyui.desktop.getDesktop().registerHandler(pygame.locals.QUIT, onQuit)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.KEYDOWN , onKeyDown)
    pyui.desktop.getDesktop().registerHandler(pyui.locals.MENU_EXIT, onQuit)

    createMenu()
    createControls()
    global running
    
    #camera flight at startup
    x = 0
    
    while x != 55:
        rotY+=3.8
        rotX+=0.5
        zoom+=0.08

        pyui.draw()
        pyui.update()
        x+=1   
        
    x = 0
    #Startposition (Arm2, Arm1)
    armState = initialArmState
    moveArmRel(0,0)
    while running:
        if go == True:
            _idlefunc()
                
        pyui.draw()
        pyui.update()
        server.handle_request()
        
    saveConfiguration()
    pyui.quit()


if __name__ == '__main__':
    run()
