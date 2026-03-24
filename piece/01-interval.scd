// intervalFlow - "Drift" a2-nsane

(

~fnsane = [178.38052971582, 255.19458012749, 884.64258085768, 1748.7291446291, 9043.8864701014, 4473.3283429036, 1257.7392833936] * 0.5;
Ora.addSynths;

)

(

{
	OraPlay(Ora(~fnsane.scramble).intervalFlow, numChannels:8)
		.clusterGrad(0.1, 0.12, 2, 8, \drpm, 0.2, 2.5, 1.0);

	7.wait;

	OraPlay(Ora(~fnsane.scramble).intervalFlow(0.06), numChannels:8)
		.clusterGrad(0.1, 0.12, 2, 8, \drpm, 0.2, 2.5, 1.0);

	7.wait;

	OraPlay(Ora(~fnsane.reverse).intervalFlow, numChannels:8)
		.clusterGrad(0.02, 0.1, 1.2, 4, \drpm, 0.1, 0.75, 1);

}.fork

)