CREATE PROCEDURE `getLowestBidsByUser` (IN user_id bigint(20))
BEGIN
	SELECT sub.* FROM (
				   SELECT * FROM bid WHERE (project_id, value) IN (
											SELECT project_id, MIN(value) FROM bid GROUP BY project_id ) 
										)sub 
	WHERE sub.user_id=user_id;
END
