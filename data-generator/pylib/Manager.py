import json, codecs
from config.config import *


class NodeManager():
    _node_info = None
    _ego_net_info = None

    @classmethod
    def load_info(cls):
        if not cls._node_info:
            nodes = json.load(codecs.open(NODE_DATA_PATH, 'r', 'utf-8-sig'))
            for node in nodes['nodes']:
                cls._node_info[node['id']] = node
        if not cls._ego_net_info:
            cls._ego_net_info = json.load(codecs.open(NODE_DATA_PATH, 'r', 'utf-8-sig'))
            pass
        pass

    @classmethod
    def get_info(cls, node):
        cls.load_info()
        return cls._node_info[node]


    @classmethod
    def create_ego_net(cls, node):
        node
        pass
