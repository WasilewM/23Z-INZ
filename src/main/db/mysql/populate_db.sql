USE cache;

INSERT into factorization_results
VALUES (1, '{"factors": [1]}');
INSERT into factorization_results
VALUES (2, '{"factors": [1, 2]}');
INSERT into factorization_results
VALUES (4, '{"factors": [1, 2, 2]}');
INSERT into factorization_results
VALUES (8, '{"factors": [1, 2, 2, 2]}');
INSERT into factorization_results
VALUES (16, '{"factors": [1, 2, 2, 2, 2]}');
INSERT into factorization_results
VALUES (32, '{"factors": [1, 2, 2, 2, 2, 2]}');
INSERT into factorization_results
VALUES (64, '{"factors": [1, 2, 2, 2, 2, 2]}');
INSERT into factorization_results
VALUES (128, '{"factors": [1, 2, 2, 2, 2, 2, 2]}');

COMMIT;