import os
import hashlib
import numpy as np
from tqdm import tqdm
from skimage.io import imread

HASH_FILTER_METHOD = 'hash'
DISTANCE_FILTER_METHOD = 'distance'


def read_data(data_path, print_errors=False):
    features_matrix, labels = [], []
    errors = []
    for label in os.listdir(data_path):
        for img_name in tqdm(os.listdir(os.path.join(data_path, label)), desc=label):
            img_path = os.path.join(data_path, label, img_name)
            try:
                img = imread(img_path)
                labels.append(label)
                features_matrix.append(img)
            except Exception as e:
                errors.append(f'{img_name} {e}')
    print(f'error: total={len(errors)}, percent={len(errors)/len(labels)}')
    if print_errors:
        for err in errors:
            print(err)
    return np.asarray(features_matrix), np.asarray(labels)


def _get_hash(features_vector):
    return hashlib.md5(features_vector).digest()


def _flatten(np_matrix):
    return np_matrix.reshape(np.prod(np_matrix.shape))


def _filter_duplicates_by_hash(features_matrix, test_features_matrix=None):
    filter_query = []
    if test_features_matrix is None:
        unique_features_vectors_set = set()
        for i in tqdm(range(features_matrix.shape[0])):
            features_sample = features_matrix[i]
            features_vector_hash = _get_hash(_flatten(features_sample))
            if features_vector_hash not in unique_features_vectors_set:
                unique_features_vectors_set.add(features_vector_hash)
                filter_query.append(i)
    else:
        unique_features_vectors_set = set([_get_hash(_flatten(test_features_matrix[i]))
                                           for i in range(test_features_matrix.shape[0])])
        for i in tqdm(range(features_matrix.shape[0])):
            features_vector_hash = _get_hash(_flatten(features_matrix[i]))
            if features_vector_hash not in unique_features_vectors_set:
                filter_query.append(i)
    return np.asarray(filter_query)


def _filter_duplicates_by_distance(features_matrix, test_features_matrix):
    filter_query = []
    if test_features_matrix is None:
        unique_features_vectors_set = set()
        for i in tqdm(range(features_matrix.shape[0])):
            features_sample = features_matrix[i]
            features_vector_hash = _get_hash(_flatten(features_sample))
            if features_vector_hash not in unique_features_vectors_set:
                unique_features_vectors_set.add(features_vector_hash)
                filter_query.append(i)
    else:
        unique_features_vectors_set = set([_get_hash(_flatten(test_features_matrix[i]))
                                           for i in range(test_features_matrix.shape[0])])
        for i in tqdm(range(features_matrix.shape[0])):
            features_vector_hash = _get_hash(_flatten(features_matrix[i]))
            if features_vector_hash not in unique_features_vectors_set:
                filter_query.append(i)
    return np.asarray(filter_query)


def get_filter_duplicates_query(features_matrix, test_features_matrix=None, method=HASH_FILTER_METHOD):
    assert method in [HASH_FILTER_METHOD, DISTANCE_FILTER_METHOD]
    return {
        HASH_FILTER_METHOD: _filter_duplicates_by_hash,
        DISTANCE_FILTER_METHOD: lambda: None
    }[method](features_matrix, test_features_matrix)
