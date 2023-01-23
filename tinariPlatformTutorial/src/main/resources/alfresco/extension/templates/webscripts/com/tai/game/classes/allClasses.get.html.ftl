<html>
	<body>
		<h1>CLASSES</h1>
		<#list classes?values as class>
			<div>
				<strong>Id: </strong><span>${class["id"]}</span><br>
				<strong>Type: </strong><span>${class["type"]}</span><br>
				<strong>Related Operators: </strong><span>[
					<#list class["relatedOperators"] as operator>
						<a href="/alfresco/s/game/operator?id=${operator.id}">${operator.name}</a><#if operator_has_next>,</#if>
					</#list>
				]</span>
			</div>
			<#if class_has_next>
				<hr color="black">
			</#if>
		</#list>
	</body>
</html>