<html>
	<body>
		<h1>CLASS</h1>
		<div>
			<strong>Id: </strong><span>${id}</span><br>
			<strong>Type: </strong><span>${type}</span><br>
			<strong>Related Operators: </strong><span>[
				<#list relatedOperators as operatorName>
					${operatorName}<#if operatorName_has_next>,</#if>
				</#list>
			]</span>
		</div>
	</body>
</html>