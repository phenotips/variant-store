# Variant Store PhenoTips Integration

PhenoTips components to enable the usage of the VariantStore within PhenoTips.

Conatains:

- `./api` 
	- the `Components` to use the Variant Store from within the xwiki injection framework,
	- the `ScriptServices` to allow the Variant Store to be used within velocity templates.
- `./ui` 
	- an HTTP endpoint to upload files to the Variant Store. Used by `patient-network` cron scripts.
	- the beginning of a rudimentary UI to manage running import jobs

