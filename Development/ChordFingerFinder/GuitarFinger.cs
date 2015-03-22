using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    abstract class CGuitarFinger
    {
        public int Fret              { get; protected set; }
        abstract public int Priority { get; protected set; }
        public List<EString> Strings { get; protected set; }

        protected int m_nPriority;

        abstract public bool CanPlay(CGuitarFinger cFngr);
        abstract protected bool GetPriorityForPlay(CFingerPosition cFngr, out int nPriority);
        abstract protected bool GetPriorityForPlay(CFingerPosition cFngr, bool fCapo, out int nPriority);
        abstract public bool IsValid();
        abstract public bool Add(CFingerPosition cPos, out bool fCapo);
        abstract public bool Add(CFingerPosition cPos);

        abstract public int GetIndex();

        abstract public override bool Equals(object obj);
    }
}
