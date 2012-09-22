<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : xmi2simpleformat.xsl
    Created on : September 22, 2012, 5:17 PM
    Author     : saricas
    Description:
        Purpose of transformation follows.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:UML = "org.omg.xmi.namespace.UML">

	<xsl:output method="text"/>

        <xsl:strip-space elements="*"/>

        <xsl:template match="XMI.header">
	</xsl:template>

	<xsl:template match="XMI.content">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="UML:Namespace.ownedElement">
            <xsl:apply-templates />
	</xsl:template>

        <xsl:template match="UML:TaggedValue.dataValue">
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

                <xsl:value-of select="$source"/> -> <xsl:value-of select="$target"/><xsl:text>&#xa;</xsl:text>
                
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

                <xsl:value-of select="$source"/> -> <xsl:value-of select="$target"/><xsl:text>&#xa;</xsl:text>
                
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

                <xsl:value-of select="$source"/> -> <xsl:value-of select="$target"/><xsl:text>&#xa;</xsl:text>
                
	</xsl:template>

</xsl:stylesheet>