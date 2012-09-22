<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : xmi2gml.xsl
    Created on : July 9, 2012, 7:47 PM
    Author     : saricas
    Description:
        Purpose of transformation follows.
-->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:UML = 'org.omg.xmi.namespace.UML'
        xmlns:y="http://www.yworks.com/xml/graphml">

	<xsl:output method="xml"/>
        
        <xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<graphml xmlns="http://graphml.graphdrawing.org/xmlns" 
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                         xmlns:y="http://www.yworks.com/xml/graphml" 
                         xmlns:yed="http://www.yworks.com/xml/yed/3" 
                         xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd">
                <key for="node" id="d1" yfiles.type="nodegraphics"/>
                <key for="edge" id="d2" yfiles.type="edgegraphics"/>
                    <xsl:apply-templates />
		</graphml>
	</xsl:template>
        
        <xsl:template match="XMI.header">
	</xsl:template>

	<xsl:template match="XMI.content">
		<xsl:apply-templates /> 
	</xsl:template>
	
	<xsl:template match="UML:Model">
		<graph id="{@name}" edgedefault="undirected">
			<xsl:apply-templates /> 
		</graph>
	</xsl:template>
        
	<xsl:template match="UML:Namespace.ownedElement">
            <xsl:apply-templates />
	</xsl:template>

        
        <xsl:template match="UML:TaggedValue.dataValue">
	</xsl:template>

	<xsl:template match="UML:Class[@name]" priority="2">
            <xsl:if test="@name!=''">
		<node id="{@name}">
                    <data key="d1">
                        <y:ShapeNode>
                        <y:Geometry height="30.0" width="30.0" />
                        <y:Fill color="#FFCC00" transparent="false"/>
                        <y:BorderStyle color="#000000" type="line" width="1.0"/>
                        <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="18.1328125" modelName="internal" modelPosition="c" textColor="#000000" visible="true" width="12.279296875" x="8.8603515625" y="5.93359375"><xsl:value-of select="@name"/></y:NodeLabel>
                        <y:Shape type="circle"/>
                        </y:ShapeNode>
                    </data>
                </node>
            </xsl:if>
	</xsl:template>
        
	<xsl:template match="UML:Interface[@name]" priority="2">
            <xsl:if test="@name!=''">
		<node id="{@name}">
                    <data key="d1">
                        <y:ShapeNode>
                        <y:Geometry height="30.0" width="30.0" />
                        <y:Fill color="#FFCC00" transparent="false"/>
                        <y:BorderStyle color="#000000" type="line" width="1.0"/>
                        <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="18.1328125" modelName="internal" modelPosition="c" textColor="#000000" visible="true" width="12.279296875" x="8.8603515625" y="5.93359375"><xsl:value-of select="@name"/></y:NodeLabel>
                        <y:Shape type="circle"/>
                        </y:ShapeNode>
                    </data>
                </node>
            </xsl:if>
	</xsl:template>
        
	<xsl:template match="UML:Dependency[@xmi.id]" priority="1">
		<xsl:variable name="src"> 
			<xsl:value-of select=".//UML:Dependency.client/UML:Class/@xmi.idref | .//UML:Dependency.client/UML:Interface/@xmi.idref | .//UML:Dependency.client/UML:Component/@xmi.idref"/> 
		</xsl:variable>
		<xsl:variable name="trg"> 
			<xsl:value-of select=".//UML:Dependency.supplier/UML:Class/@xmi.idref | .//UML:Dependency.supplier/UML:Interface/@xmi.idref | .//UML:Dependency.supplier/UML:Component/@xmi.idref"/> 
		</xsl:variable>
		<xsl:variable name="source"> 
			<xsl:for-each select="//UML:Class | //UML:Interface | //UML:Component">
				<xsl:if test="@xmi.id=$src">
					<xsl:value-of select="substring-before(./@name, '.java')"/>
				</xsl:if>  
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="target"> 
			<xsl:for-each select="//UML:Class | //UML:Interface | //UML:Component">
				<xsl:if test="@xmi.id=$trg">
					<xsl:value-of select="./@name"/>
				</xsl:if>  
			</xsl:for-each>
		</xsl:variable>
                
		<edge id="{@xmi.id}" source="{$source}" target="{$target}">
                    <data key="d2">
                        <y:PolyLineEdge>
                        <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>
                        <y:LineStyle color="#000000" type="line" width="1.0"/>
                        <y:Arrows source="none" target="none"/>
                        <y:BendStyle smoothed="false"/>
                        </y:PolyLineEdge>
                    </data>
                </edge>
	</xsl:template>

	<xsl:template match="UML:Usage[@xmi.id]">
		<xsl:variable name="src"> 
			<xsl:value-of select=".//UML:Dependency.supplier/UML:Class/@xmi.idref | .//UML:Dependency.supplier/UML:Interface/@xmi.idref | .//UML:Dependency.supplier/UML:Component/@xmi.idref"/> 
		</xsl:variable>
		<xsl:variable name="trg"> 
			<xsl:value-of select=".//UML:Dependency.client/UML:Class/@xmi.idref | .//UML:Dependency.client/UML:Interface/@xmi.idref | .//UML:Dependency.client/UML:Component/@xmi.idref"/> 
		</xsl:variable>
		<xsl:variable name="source"> 
			<xsl:for-each select="//UML:Class | //UML:Interface | //UML:Component">
				<xsl:if test="@xmi.id=$src">
					<xsl:value-of select="./@name"/>
				</xsl:if>  
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="target"> 
			<xsl:for-each select="//UML:Class | //UML:Interface | //UML:Component">
				<xsl:if test="@xmi.id=$trg">
					<xsl:value-of select="./@name"/>
				</xsl:if>  
			</xsl:for-each>
		</xsl:variable>
                
		<edge id="{@xmi.id}" source="{$source}" target="{$target}">
                    <data key="d2">
                        <y:PolyLineEdge>
                        <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>
                        <y:LineStyle color="#000000" type="line" width="1.0"/>
                        <y:Arrows source="none" target="standart"/>
                        <y:BendStyle smoothed="false"/>
                        </y:PolyLineEdge>
                    </data>
                </edge>
	</xsl:template>
        
	<xsl:template match="UML:Association">
		<xsl:variable name="src"> 
			<xsl:value-of select=".//UML:Association.connection/UML:AssociationEnd[1]/UML:AssociationEnd.participant/UML:Class/@xmi.idref | .//UML:Association.connection/UML:AssociationEnd[1]/UML:AssociationEnd.participant/UML:Interface/@xmi.idref"/> 
		</xsl:variable>
		<xsl:variable name="trg"> 
			<xsl:value-of select=".//UML:Association.connection/UML:AssociationEnd[2]/UML:AssociationEnd.participant/UML:Class/@xmi.idref | .//UML:Association.connection/UML:AssociationEnd[2]/UML:AssociationEnd.participant/UML:Interface/@xmi.idref"/> 
		</xsl:variable>
		<xsl:variable name="source"> 
			<xsl:for-each select="//UML:Class | //UML:Interface">
				<xsl:if test="@xmi.id=$src">
					<xsl:value-of select="./@name"/>
				</xsl:if>  
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="target"> 
			<xsl:for-each select="//UML:Class | //UML:Interface">
				<xsl:if test="@xmi.id=$trg">
					<xsl:value-of select="./@name"/>
				</xsl:if>  
			</xsl:for-each>
		</xsl:variable>
		
		<edge id="{@xmi.id}" source="{$source}" target="{$target}">
                    <data key="d2">
                        <y:PolyLineEdge>
                        <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>
                        <y:LineStyle color="#000000" type="line" width="1.0"/>
                        <y:Arrows source="none" target="standart"/>
                        <y:BendStyle smoothed="false"/>
                        </y:PolyLineEdge>
                    </data>
                </edge>
	</xsl:template>

</xsl:stylesheet>